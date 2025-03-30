#include <torch/csrc/jit/frontend/tracer.h>
#include <torch/csrc/jit/ir/alias_analysis.h>
#include <torch/csrc/jit/ir/graph_utils.h>
#include <torch/csrc/jit/passes/onnx.h>
#include <torch/csrc/onnx/onnx.h>
#include <torch/script.h>
#include <torch/torch.h>

#include <iostream>
#include <sstream>
#include <memory>
#include <vector>
#include <unordered_map>
#include <unordered_set>
#include <list>
#include <tuple>
#include <set>
#include <queue>
#include <stack>
#include <cassert>
#include <cstdlib> // for std::exit

enum OPType{
    module,
    node
};
class OP{
    private:
        torch::jit::script::Module module;
        std::string module_name;
        torch::jit::Node* node;
        OPType type;
    public:
        OP(torch::jit::script::Module module, std::string module_name){
            this->module = module.copy();
            this->module_name = module_name;
            this->node = nullptr;
            this->type = OPType::module;
        }
        OP(torch::jit::Node* node){
            this->module;
            this->node = node;
            this->type = OPType::node;
        }
        OPType getType(){ return this->type;}

        torch::jit::script::Module getModule(){ return this->module;}
        torch::jit::Node* getNode(){ return this->node;}
        std::string getModuleName(){ return this->module_name;}
        std::string getModuleOPName(){ return this->module.type().get()->name()->name();}
        std::string getModuleQualifiedName(){ return this->module.type().get()->name()->qualifiedName();}
        std::string getNodeOPName(){ return this->node->schema().name();}

        ~OP(){}
};

class IRTrace{
    private:
        torch::jit::script::Module module;

        // HW2-4-2
        std::list<std::pair<std::string, torch::jit::script::Module>> module_list;
        std::list<OP> OP_list;
        torch::jit::Stack stack;
        std::map<torch::jit::Value*, torch::jit::IValue> map_method_outputs; // node->output() map to output tensor
        
        // HW2-4-3
        std::vector<std::tuple<OP, torch::Tensor, torch::Tensor>> activation_list;

    public:
        IRTrace(){}
        ~IRTrace(){}
        void loadModel(const std::string file_name){
            try{
                this->module = torch::jit::load(file_name);
            }
            catch(const c10::Error& e){
                std::cerr << "Error loading the model\n";
				exit(1);
            }
			std::cout << "Load the torchscript model, " + file_name + ", successfully \n";
        }
        void HW2_4_1(){
            std::cout << "======================================================================\n";
            std::cout << "2-4-1 Calculate memory requirements for storing the model weights\n";
            std::cout << "======================================================================\n";
            
            int total_weights_memory = 0;		

            for(const auto& parameter_pair : this->module.named_parameters()){
                auto parameter_name = parameter_pair.name;
                auto parameter = parameter_pair.value;

                auto dtype = parameter.scalar_type();
                auto sizeof_dtype = parameter.element_size();
                auto shape = parameter.sizes();
                auto dims = shape.size();

                int weight_size = 1;
                for(int dim=0; dim<dims; dim++){
                    weight_size *= shape[dim];
                }

                int weight_memory = sizeof_dtype * weight_size;
                total_weights_memory += weight_memory;

                std::cout << parameter_name << "\n";
                std::cout << "  |-- shape  " << shape << " (" << weight_size << ")\n";
                std::cout << "  |-- dtype  " << dtype << " (" << sizeof_dtype << " bytes)\n";
                std::cout << "  \\-- memory " << weight_memory << " bytes\n";
            }
            std::cout << "----------------------------------------------------------------------\n";
            std::cout << "Total memory usage of model weights: " << total_weights_memory << " bytes\n";           
            std::cout << "======================================================================\n";
        }


        void getConstantParam(torch::jit::Node* parent_node, std::string parme_type_string) {
            // %21 : bool = prim::Constant[value=1]()
            if (parent_node->hasAttributes()) {
                auto parent_attr_names = parent_node->attributeNames();
                auto parent_attr       = parent_attr_names[0];
                if (strcmp(parent_attr.toUnqualString(), "value") == 0) {
                    auto parent_attr_kind = parent_node->kindOfS("value");
                    switch (parent_attr_kind) {
                        case torch::jit::AttributeKind::i: {
                            if (parme_type_string.substr(0, 4) == "bool") {
                                stack.push_back(parent_node->i(parent_attr) == 1);
                            } else {
                                stack.push_back(parent_node->i(parent_attr));
                            }
                            break;
                        }
                        case torch::jit::AttributeKind::f: {
                            stack.push_back(parent_node->f(parent_attr));
                            break;
                        }
                        case torch::jit::AttributeKind::t: {
                            stack.push_back(parent_node->t(parent_attr));
                            break;
                        }
                    }
                }
            } else {
                // Constant none value
                stack.push_back(torch::jit::IValue());
            }
        }
        void getListParam(torch::jit::Node* parent_node) {
            // %11 : int[] = prim::ListConstruct(%9, %9)
            std::vector<int64_t>       list_i;
            std::vector<float>         list_f;
            std::vector<torch::Tensor> list_tensor;
            for (const auto& parent_in : parent_node->inputs()) {
                if (map_method_outputs.find(parent_in) != map_method_outputs.end()) { /* cat input tensor[] */
                    list_tensor.push_back(map_method_outputs[parent_in].toTensor());
                } else {
                    auto grand_node      = parent_in->node();
                    auto grand_node_attr = grand_node->attributeNames()[0];

                    if (strcmp(grand_node_attr.toUnqualString(), "value") == 0) {
                        auto grand_node_kind = grand_node->kindOfS("value");

                        switch (grand_node_kind) {
                            case torch::jit::AttributeKind::i: list_i.push_back(grand_node->i(grand_node_attr)); break;
                            case torch::jit::AttributeKind::f: list_f.push_back(grand_node->f(grand_node_attr)); break;
                        }
                        // std::cout << node->inputs[i].
                    }
                }
            }
            if (list_i.size() == parent_node->inputs().size())
                stack.push_back(torch::jit::IValue(list_i));
            else if (list_f.size() == parent_node->inputs().size())
                stack.push_back(torch::jit::IValue(list_f));
            else
                stack.push_back(torch::jit::IValue(list_tensor));  // 推回空 list
        }
        void getGetAttrParam(torch::jit::Node* parent_node, torch::jit::named_attribute_list attribut_list) {
            bool       isParameterArg = false;
            at::Tensor parameter;
            auto       parent_attr_name = parent_node->s(parent_node->attributeNames()[0]);
            for (const auto& param : attribut_list) {
                // std::cout << param.name << "-" << in.name() << " ";
                if (param.name == parent_attr_name) {
                    isParameterArg = true;
                    // std::cout << param.value;
                    parameter = param.value.toTensor();
                    break;
                }
            }
            stack.push_back(parameter);
        }


        std::string getModuleQualifiedName(torch::jit::script::Module &module){
            return module.type().get()->name()->qualifiedName();
        }
        std::string getModuleOPName(torch::jit::script::Module &module){
            return module.type().get()->name()->name();
        }
        std::string getNodeAttrName(torch::jit::Node* node){
            auto forward_mod_attrnode = node->inputs()[0]->node();
            auto forward_attr_name    = forward_mod_attrnode->s(forward_mod_attrnode->attributeNames()[0]); 
            return forward_attr_name;
        }


        
        void getModuleList(){

            // module name      qualifiedName
            // (Alexnet)        __torch__.torchvision.models.alexnet.AlexNet
            // features         __torch__.torch.nn.modules.container.Sequential
            // features.0       __torch__.torch.nn.modules.conv.Conv2d
            // features.1       __torch__.torch.nn.modules.activation.ReLU
            // features.2       __torch__.torch.nn.modules.pooling.MaxPool2d
            // features.3       __torch__.torch.nn.modules.conv.___torch_mangle_0.Conv2d
            // features.4       __torch__.torch.nn.modules.activation.___torch_mangle_1.ReLU
            // features.5       __torch__.torch.nn.modules.pooling.___torch_mangle_2.MaxPool2d
            // features.6       __torch__.torch.nn.modules.conv.___torch_mangle_3.Conv2d
            // features.7       __torch__.torch.nn.modules.activation.___torch_mangle_4.ReLU
            // features.8       __torch__.torch.nn.modules.conv.___torch_mangle_5.Conv2d
            // features.9       __torch__.torch.nn.modules.activation.___torch_mangle_6.ReLU
            // features.10      __torch__.torch.nn.modules.conv.___torch_mangle_7.Conv2d
            // features.11      __torch__.torch.nn.modules.activation.___torch_mangle_8.ReLU
            // features.12      __torch__.torch.nn.modules.pooling.___torch_mangle_9.MaxPool2d
            // avgpool          __torch__.torch.nn.modules.pooling.AdaptiveAvgPool2d
            // classifier       __torch__.torch.nn.modules.container.___torch_mangle_15.Sequential
            // classifier.0     __torch__.torch.nn.modules.dropout.Dropout
            // classifier.1     __torch__.TiledLinear_slice
            // classifier.2     __torch__.torch.nn.modules.activation.___torch_mangle_10.ReLU
            // classifier.3     __torch__.torch.nn.modules.dropout.___torch_mangle_11.Dropout
            // classifier.4     __torch__.___torch_mangle_12.TiledLinear_slice
            // classifier.5     __torch__.torch.nn.modules.activation.___torch_mangle_13.ReLU
            // classifier.6     __torch__.___torch_mangle_14.TiledLinear_slice
            for(auto submodule_pair : this->module.named_modules()){
                auto submodule = submodule_pair.value;
                auto submodule_name = submodule_pair.name;
                auto submodule_qualifiedName = submodule.type().get()->name()->qualifiedName();

                // If "container" doesn't exist, which submodule is a operator module
                // if( submodule_name.find("__torch__.torch.nn.modules")!=std::string::npos &&
                //     submodule_name.find("container")==std::string::npos){
                    std::pair<std::string, torch::jit::script::Module> p(submodule_name, submodule);
                    this->module_list.push_back(p);
                // }
            }
        }
        void recursivelyGetOPList(){
            // clone the module_list to OP_list
            for(auto submodule_pair : this->module_list){
                auto submodule_name = submodule_pair.first;
                auto submodule = submodule_pair.second;

                OP op(submodule, submodule_name);
                OP_list.push_back(op);
            }

            // for(auto op : OP_list){
            //     std::cout << op.getModuleName() << "\t" << op.getModuleQualifiedName() << "\n";
            // }

            // recusively check if there is operator node under submodule
            std::list<OP> OP_list_copy(OP_list);
            torch::jit::Node *found_op_node;
            std::string prev_module_name;

            for(auto op : OP_list_copy){
                // there should only be modules in OP_list_copy
                if(op.getType()!=OPType::module) continue;
                if(op.getModule().named_children().size()==0) continue;

                auto graph = op.getModule().get_method("forward").graph();
                for(auto node : graph->nodes()){
                    // if find a operator node, insert it to correct position in OP_list

                    if(node->maybeOperator()){
                        bool found_prev_module = false;
                        std::_List_iterator<OP> it;
                        for(it=OP_list.begin(); it!=OP_list.end(); it++){
                            // prev module has been found, and now position is where to insert op node
                            if(found_prev_module && it->getModuleName().find(prev_module_name)==std::string::npos){
                                break;
                            }
                            // prev module hasn't been found, try to find it
                            if(!found_prev_module && it->getModuleName().find(prev_module_name)!=std::string::npos){
                                found_prev_module = true;
                            }
                        }
                        OP_list.insert(it, OP(node));
                    }
                    else if(node->kind()==torch::prim::CallMethod){
                        prev_module_name = getNodeAttrName(node);
                    }
                }
            }
            // for(auto op : OP_list){
            //     if(op.getType()==OPType::module){
            //         std::cout << op.getModuleName() << "\t" << op.getModuleQualifiedName() << "\n";
            //     }
            //     else{
            //         std::cout << op.getNodeOPName() << "\n";
            //     }
            // }
        }

        void HW2_4_2(){
            std::cout << "======================================================================\n";
            std::cout << "2-4-2 Export A list of operators with input tensor shape and output tensor shape\n";
            std::cout << "======================================================================\n";

            torch::Tensor input, output;
            input = torch::randn({1, 3, 224, 224});

            getModuleList();

            recursivelyGetOPList();

            for(auto op : this->OP_list){
                if(op.getType()==OPType::module){
                    // the module with qualified name "container" ===> there are submodules under this module
                    // the module with qualified name "__torch__.torch.nn.modules" ===> it's a operator module, just run the module to get result
                    // the module with qualified name "TiledLinear" ===> it's the modified linear, which should be analyzed later
                    if( (op.getModuleQualifiedName().find("__torch__.torch.nn.modules")!=std::string::npos || op.getModuleQualifiedName().find(".TiledLinear_slice")!=std::string::npos) &&
                        op.getModuleQualifiedName().find("container")==std::string::npos){
                        output = op.getModule().forward({input}).toTensor();
                        std::stringstream input_shape, output_shape;
                        input_shape << input.sizes();
                        output_shape << output.sizes();
                                    
                        std::cout   << std::left << std::setw(20) << op.getModuleOPName()
                                    << "input shape: " << std::left << std::setw(20) << input_shape.str() 
                                    << "output shape: " << std::left << std::setw(20) << output_shape.str()  << "\n";
                        
                        // prepare for HW2-4-3
                        std::tuple<OP, torch::Tensor, torch::Tensor> t(op, input, output);
                        activation_list.push_back(t);

                        input = output;
                    }
                }
                if(op.getType()==OPType::node){
                    auto node = op.getNode();
                    auto operation = node->getOperation();
                    auto schema = node->schema();

                    stack.clear();

                    auto input_nodes = node->inputs();
					int  idx         = 0;

					for (const auto& param : schema.arguments()) {
						auto input_node = input_nodes[idx]->node();
						idx++;
						switch (input_node->kind()) {
                            case torch::prim::CallMethod: {
                                // the operator node call itself
                                if(param.name().find("self")!=std::string::npos){
                                    stack.push_back(input);
                                }
                            }
							case torch::prim::Constant: {
                                // std::cout << "torch::prim::Constant" << "\n";
								getConstantParam(input_node, param.type()->str());
								break;
							}

							case torch::prim::ListConstruct: {
                                // std::cout << "torch::prim::ListConstruct" << "\n";
								getListParam(input_node);
								break;
							}

							case torch::prim::GetAttr: {
                                // std::cout << "torch::prim::GetAttr" << "\n";
								getGetAttrParam(input_node, this->module.named_attributes());
								break;
							}

							case torch::prim::Param: {
                                // std::cout << "torch::prim::Param" << "\n";
								stack.push_back(input);
								break;
							}
						}	
                        
					}		
                    operation(stack);   
                    output=stack.back().toTensor();	
                    std::stringstream input_shape, output_shape;
                    input_shape << input.sizes();
                    output_shape << output.sizes();
                    
                    std::cout   << std::left << std::setw(20) << op.getNodeOPName()
                                << "input shape: " << std::left << std::setw(20) << input_shape.str() 
                                << "output shape: " << std::left << std::setw(20) << output_shape.str()  << "\n";

                    // prepare for HW2-4-3
                    std::tuple<OP, torch::Tensor, torch::Tensor> t(op, input, output);
                    activation_list.push_back(t);

                    input = output;
                }
            }
            std::cout << "======================================================================\n";
        }

        void HW2_4_3(){
            std::cout << "======================================================================\n";
            std::cout << "2-4-3. Calculate memory requirements for storing the activations\n";
            std::cout << "======================================================================\n";

            int total_activations_memory = 0;		

            for(auto& activation_tuple : activation_list){
                auto op = std::get<0>(activation_tuple);
                auto op_name = (op.getType()==OPType::module) ? op.getModuleOPName() : op.getNodeOPName();
                auto input = std::get<1>(activation_tuple);
                auto output = std::get<2>(activation_tuple);

                auto dtype = output.scalar_type();
                auto sizeof_dtype = output.element_size();
                auto shape = output.sizes();
                auto dims = shape.size();

                int weight_size = 1;
                for(int dim=0; dim<dims; dim++){
                    weight_size *= shape[dim];
                }

                int weight_memory = sizeof_dtype * weight_size;
                total_activations_memory += weight_memory;

                std::cout << op_name << "\n";
                std::cout << "  |-- shape  " << shape << " (" << weight_size << ")\n";
                std::cout << "  |-- dtype  " << dtype << " (" << sizeof_dtype << " bytes)\n";
                std::cout << "  \\-- memory " << weight_memory << " bytes\n";
            }
            std::cout << "----------------------------------------------------------------------\n";
            std::cout << "Total memory usage of model activations: " << total_activations_memory << " bytes\n"; 
            std::cout << "======================================================================\n";
        }
        void HW2_4_4(){
            std::cout << "======================================================================\n";
            std::cout << "2-4-4. Calculate computation requirements\n";
            std::cout << "======================================================================\n";

            int total_macs = 0;	

            for(auto& activation_tuple : activation_list){
                auto op = std::get<0>(activation_tuple);
                auto op_name = (op.getType()==OPType::module) ? op.getModuleOPName() : op.getNodeOPName();
                auto input = std::get<1>(activation_tuple);
                auto output = std::get<2>(activation_tuple);

                auto input_shape = input.sizes();
                auto output_shape = output.sizes();

                std::stringstream input_shape_ss, output_shape_ss;
                input_shape_ss << input_shape;
                output_shape_ss << output_shape;

                int macs = 0;

                if(op.getType()==OPType::node){
                    macs = 0;
                }
                else if(op.getModuleOPName().find("Conv")!=std::string::npos){
                    // Number of Operations=Output Height×Output Width
                    //                      ×Kernel Height×Kernel Width
                    //                      ×Input Channels×Output Channels
                    auto attributes = op.getModule().named_attributes();
                    for(auto attribute_pair : attributes){
                        if(attribute_pair.name.find("weight")==std::string::npos) continue;

                        auto weight_shape = attribute_pair.value.toTensor().sizes();
                        macs =   output_shape[2]*output_shape[3]
                                *weight_shape[2]*weight_shape[3]
                                *input_shape[1]*output_shape[1];
                    }
                }
                else if(op.getModuleOPName().find("Linear")!=std::string::npos){
                    // Number of Operations=Input Features×Output Features
                    auto attributes = op.getModule().named_attributes();
                    for(auto attribute_pair : attributes){
                        if(attribute_pair.name.find("weight")==std::string::npos) continue;

                        auto weight_shape = attribute_pair.value.toTensor().sizes();
                        macs =   weight_shape[0]*weight_shape[1];
                    }
                }
                else{
                    macs = 0;
                }


                total_macs += macs;

                std::cout   << std::left << std::setw(20) << op_name
                            << "input shape: " << std::left << std::setw(20) << input_shape_ss.str() 
                            << "output shape: " << std::left << std::setw(20) << output_shape_ss.str() 
                            << "macs: " << (macs==0 ? "N/A" : std::to_string(macs))  << "\n";

            }
            std::cout << "----------------------------------------------------------------------\n";
            std::cout << "Total macs of model: " << total_macs << "\n"; 
            std::cout << "======================================================================\n";
        }
};



int main(int argc, char *argv[]){

    if (argc < 2) {
        printf("Usage: %s <filename>\n", argv[0]);
        return 1;
    }

    IRTrace *HW2_4 = new IRTrace;
    HW2_4->loadModel(argv[1]);
    HW2_4->HW2_4_1();   std::cout << "\n\n";
    HW2_4->HW2_4_2();   std::cout << "\n\n";
    HW2_4->HW2_4_3();   std::cout << "\n\n";
    HW2_4->HW2_4_4();   std::cout << "\n\n";
}