#include <torch/script.h> // One-stop header.
#include <torch/csrc/jit/api/module.h>
#include <torch/csrc/jit/jit_log.h>
#include <torch/csrc/jit/ir/ir.h>

#include <iostream>
#include <memory>

using namespace torch::jit;

int main(int argc, const char* argv[]) {
  if (argc != 2) {
    std::cerr << "usage: example-app <path-to-exported-script-module>\n";
    return -1;
  }

  set_jit_logging_levels("GRAPH_DUMP");

  torch::jit::script::Module module;
  try {
    // Deserialize the ScriptModule from a file using torch::jit::load().
    module = torch::jit::load(argv[1]);
  }
  catch (const c10::Error& e) {
    std::cerr << "error loading the model\n";
    return -1;
  }

  std::cout << "load the torchscript model, " + std::string(argv[1]) + ", successfully \n";

  // Create a vector of inputs.
  std::vector<torch::jit::IValue> inputs;	
  inputs.push_back(torch::ones({1, 3, 224, 224}));

  // Execute the model and turn its output into a tensor.
  at::Tensor output = module.forward(inputs).toTensor();
  std::cout << output.slice(/*dim=*/1, /*start=*/0, /*end=*/5) << '\n';

  //dump the model information
  // source code can be found in 
  // https://github.com/pytorch/pytorch/blob/main/torch/csrc/jit/api/module.cpp
  std::cout << module.dump_to_str(true,false,false) << " (\n";
  
}
