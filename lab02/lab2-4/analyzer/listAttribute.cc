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
	this->module.dump(1,0,0);
}


void IRTracer::ListParameters() {

	for (const auto& param : this->module.parameters()) {
		auto param_type   = param.scalar_type();
		auto param_tensor = param.sizes();		
		std::cout << param_type <<  "," << param_tensor << std::endl;
	}	
}

void IRTracer::ListFirstAttributes() {
	auto attribut_list =  this->module.named_attributes();
	for (const auto& param : attribut_list) {		
		std:: cout << param.name << std::endl;		
		}
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

	std::cout << "==== List Parameters ==== " << std::endl; 
	tracer->ListParameters();
	std::cout << "==== List First Attributes ==== " << std::endl;
	tracer->ListFirstAttributes();
	
	return 0;
  }
  catch (const c10::Error& e) {
    std::cerr << "error loading the model\n";
    return -1;
  }

	
}
