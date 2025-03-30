import torch
from torchvision import models, datasets, transforms as T
mobilenet_v2 = models.mobilenet_v2(pretrained=True)

image_height = 224
image_width = 224
x = torch.randn(1, 3, image_height, image_width, requires_grad=True)
torch_out = mobilenet_v2(x)

# Export the model
torch.onnx.export(mobilenet_v2,              # model being run
                  x,                         # model input (or a tuple for multiple inputs)
                  "mobilenetv2-10.onnx", # where to save the model (can be a file or file-like object)
                  export_params=True,        # store the trained parameter weights inside the model file
                  opset_version=12,          # the ONNX version to export the model to
                  do_constant_folding=True,  # whether to execute constant folding for optimization
                  input_names = ['input'],   # the model's input names
                  output_names = ['output']) # the model's output names


resnet50 = models.resnet50(pretrained=True)
# Export the model
torch.onnx.export(resnet50,              # model being run
                  x,                         # model input (or a tuple for multiple inputs)
                  "resnet50_float.onnx", # where to save the model (can be a file or file-like object)
                  export_params=True,        # store the trained parameter weights inside the model file
                  opset_version=12,          # the ONNX version to export the model to
                  do_constant_folding=True,  # whether to execute constant folding for optimization
                  input_names = ['input'],   # the model's input names
                  output_names = ['output']) # the model's output names



