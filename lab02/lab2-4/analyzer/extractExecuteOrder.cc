#include "IRTracer.hh"

#include <torch/csrc/jit/frontend/tracer.h>
#include <torch/csrc/jit/ir/alias_analysis.h>
#include <torch/csrc/jit/ir/graph_utils.h>
#include <torch/csrc/jit/passes/onnx.h>
#include <torch/csrc/onnx/onnx.h>
#include <torch/script.h>
#include <torch/torch.h>

#include <iostream>
#include <memory>
#include <unordered_map>

int total_macs = 0;

void IRTracer::loadModel(std::string modelFileName) {
	try {
		// Deserialize the ScriptModule from a file using torch::jit::load().
		this->module = torch::jit::load(modelFileName);
	} catch (const c10::Error& e) {
		std::cerr << "error loading the model\n";
		exit(1);
	}
	std::cout << "load the torchscript model, " + modelFileName + ", successfully \n";
}

void  IRTracer::traceModuleOrder(){
	std::string module_name = (this->module.type().get()->name())->qualifiedName();
	ir_dag.addNode(DAG::DAG_PREDEFINE::START_NODE, "start");
	ir_dag.addNode(DAG::DAG_PREDEFINE::END_NODE, "end");
	ir_dag.addNode(DAG::DAG_PREDEFINE::INPUT_MODULE, module_name);
	ir_dag.addEdge(DAG::DAG_PREDEFINE::START_NODE, DAG::DAG_PREDEFINE::INPUT_MODULE);
	ir_dag.addEdge(DAG::DAG_PREDEFINE::INPUT_MODULE, DAG::DAG_PREDEFINE::END_NODE);
	traceModuleOrder(&this->module,DAG::DAG_PREDEFINE::INPUT_MODULE);
}

std::string IRTracer::searchChildModule(torch::jit::script::Module* module, std::string searchName){
	for (auto&& child : module->named_children()) {		
		if (child.name == searchName) {			
			auto  submodule_name = (*(child.value.type().get()->name())).qualifiedName();
			return submodule_name;
		}
	}
	return "";
}

void IRTracer::traceModuleOrder(torch::jit::script::Module* module, int parentIR_id)
{
	auto graph = module->get_method("forward").graph();
	
	if (module->named_children().size() > 0) {
		int last_entry = ir_dag.findEndWith(parentIR_id);  
		int last_child_id;

		torch::jit::Node* start_node = nullptr;
		
		for (const auto& node : graph->nodes()) {
			if (node->kind() == torch::prim::CallMethod && node->inputs()[CallMethodInput::input] == graph->inputs()[GraphInput::x]) {
				
				start_node = node;
				{
					auto forward_mod_attrnode = start_node->inputs()[CallMethodInput::object]->node();
					auto forward_attr_name    = forward_mod_attrnode->s(forward_mod_attrnode->attributeNames()[0]);
					
					std::string child_module_name = searchChildModule(module,forward_attr_name);					
					int child_module_id = ir_dag.addNode(child_module_name);				
					ir_dag.replaceStartPoint(parentIR_id, child_module_id);
					
					std::cout << "---add first child and replace first child as entry---" << std::endl;
					last_child_id = child_module_id;
				}

				torch::jit::Value* current_value;
				current_value = start_node->output();

				std::cout << "current_value:" << current_value->debugName() << std::endl;
				std::cout << "find node in graph:" << std::endl;
				int         child_module_id   = 0;
				std::string child_module_name = "child";
				for (const auto& node : graph->nodes()) {
					if (node->kind() == torch::prim::CallMethod) {
						if (node->inputs()[CallMethodInput::input] == current_value) {

							auto forward_mod_attrnode = node->inputs()[CallMethodInput::object]->node();
							auto forward_attr_name = forward_mod_attrnode->s(forward_mod_attrnode->attributeNames()[0]);

							child_module_name = searchChildModule(module,forward_attr_name);
							child_module_id = ir_dag.addNode(child_module_name);
							ir_dag.addEdge(last_child_id, child_module_id);
							std::cout << "---add child and link edge--" << std::endl;							
							last_child_id = child_module_id;
						}
						current_value = node->output();
					}
				}
				std::cout << "---make parent node as exit--" << std::endl;
				ir_dag.addEdge(last_child_id, parentIR_id); 
			}
		}
	}
	ir_dag.toGraphviz();
}


int main(int argc, const char* argv[]) {
  if (argc != 2) {
    std::cerr << "usage: example-app <path-to-exported-script-module>\n";
    return -1;
  }

  try {
    // Deserialize the ScriptModule from a file using torch::jit::load().
	IRTracer* tracer = new IRTracer("");
	tracer->loadModel(argv[1]);
	tracer->traceModuleOrder();

	return 0;
  }
  catch (const c10::Error& e) {
    std::cerr << "error loading the model\n";
    return -1;
  }

	
}
