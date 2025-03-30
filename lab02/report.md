# (NTHU_李承澔_113062559) AIAS 2024 Lab 2 HW Submission Template

:::info
1. <font color="red">請不要用這份template 交作業</font>, 建立一個新的codimd 檔案, 然後copy & paste 這個template 到你創建的檔案做修改。
2. 請修改你的學號與姓名在上面的title 跟這裡, 以避免TA 修改作業時把檔案跟人弄錯了
3. 在Playlab 作業中心繳交作業時, 請用你創建的檔案鏈結繳交, 其他相關的資料與鏈結請依照Template 規定的格式記載於codimd 上。

記得在文件標題上修改你的 (校名_學號_姓名)
:::

[toc]

## Gitlab code link
:::info
Please paste the link to your private Gitlab repository for this homework submission here. 
:::

- [Gitlab link](https://course.playlab.tw/git/chngh0207/lab02)  


## HW 2-1 Model Analysis Using Pytorch

### 2-1-1. Calculate the number of model parameters：

#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::

```python=
import torchvision.models as models

model = models.googlenet(pretrained=True)
print(model)

input_shape = (3, 224, 224)
```
```python=
total_params = sum(p.numel() for p in model.parameters())
trainable_params = sum(p.numel() for p in model.parameters() if p.requires_grad)
print("Total number of parameters: ", total_params)
print("Number of trainable parameters: ", trainable_params)
```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::

![](https://course.playlab.tw/md/uploads/53b53da1-8bc6-4e2a-8a56-011a67cda77c.png)



### 2-1-2. Calculate memory requirements for storing the model weights.
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::

```python=
# torch.Tensor.element_size returns the size in bytes of an individual element.
param_size = sum(p.numel() * p.element_size() for p in model.parameters())
print(f"Total memory for parameters: {param_size} byte")
```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/6e27a571-b04c-47f8-a23e-8e298a55e923.png)



### 2-1-3. Use Torchinfo to print model architecture including the number of parameters and the output activation size of each layer 
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
# Use summary function in torchinfo, not torchsummary
from torchinfo import summary

summary(model=model, input_size=(1, 3, 224, 224))
```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/5cc4509c-34ac-493c-ade9-b02719a67dda.png)




### 2-1-4. Calculate computation requirements
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
import torch
import torch.nn as nn
import math

def calculate_output_shape(input_shape, layer):
    # Calculate the output shape for Conv2d, MaxPool2d, and Linear layers
    if isinstance(layer, (nn.Conv2d, nn.MaxPool2d)):
        kernel_size = (
            layer.kernel_size
            if isinstance(layer.kernel_size, tuple)
            else (layer.kernel_size, layer.kernel_size)
        )
        stride = (
            layer.stride
            if isinstance(layer.stride, tuple)
            else (layer.stride, layer.stride)
        )
        padding = (
            layer.padding
            if isinstance(layer.padding, tuple)
            else (layer.padding, layer.padding)
        )
        dilation = (
            layer.dilation
            if isinstance(layer.dilation, tuple)
            else (layer.dilation, layer.dilation)
        )
        ceil_mode = False
        if isinstance(layer, nn.MaxPool2d):
            ceil_mode = layer.ceil_mode

        if(ceil_mode):
            output_height = math.ceil((
                input_shape[1] + 2 * padding[0] - dilation[0] * (kernel_size[0] - 1) - 1
            ) // stride[0] + 1)
            output_width = math.ceil((
                input_shape[2] + 2 * padding[1] - dilation[1] * (kernel_size[1] - 1) - 1
            ) // stride[1] + 1)
        else:
            output_height = math.floor((
                input_shape[1] + 2 * padding[0] - dilation[0] * (kernel_size[0] - 1) - 1
            ) // stride[0] + 1)
            output_width = math.floor((
                input_shape[2] + 2 * padding[1] - dilation[1] * (kernel_size[1] - 1) - 1
            ) // stride[1] + 1)
            
        return (
            layer.out_channels if hasattr(layer, "out_channels") else input_shape[0],
            output_height,
            output_width,
        )
    elif isinstance(layer, nn.Linear):
        # For Linear layers, the output shape is simply the layer's output features
        return (layer.out_features,)
    elif isinstance(layer, nn.BatchNorm2d):
        # For Linear layers, the output shape is simply the layer's output features
        return (layer.out_features,)
    else:
        return input_shape


def calculate_macs(layer, input_shape, output_shape):
    # Calculate MACs for Conv2d and Linear layers
    if isinstance(layer, nn.Conv2d):
        kernel_ops = (
            layer.kernel_size[0]
            * layer.kernel_size[1]
            * (layer.in_channels / layer.groups)
        )
        output_elements = output_shape[1] * output_shape[2]
        macs = int(kernel_ops * output_elements * layer.out_channels)
        return macs
    elif isinstance(layer, nn.Linear):
        # For Linear layers, MACs are the product of input features and output features
        macs = int(layer.in_features * layer.out_features)
        return macs
    else:
        return 0


input_shape = (3, 224, 224)
total_macs = 0

# Iterate through the layers of the model
for name, layer in model.named_modules():
    if isinstance(layer, (nn.Conv2d, nn.MaxPool2d, nn.ReLU, nn.Linear)):
        output_shape = calculate_output_shape(input_shape, layer)
        macs = calculate_macs(layer, input_shape, output_shape)
        total_macs += macs
        if isinstance(layer, (nn.Conv2d, nn.Linear)):
            print(
                f"Layer: {name}, Type: {type(layer).__name__}, Input Shape: {input_shape}, Output Shape: {output_shape}, MACs: {macs}"
            )
        elif isinstance(layer, nn.MaxPool2d):
            # Also print shape transformation for MaxPool2d layers (no MACs calculated)
            print(
                f"Layer: {name}, Type: {type(layer).__name__}, Input Shape: {input_shape}, Output Shape: {output_shape}, MACs: N/A"
            )
        input_shape = output_shape  # Update the input shape for the next layer

print(f"Total MACs: {total_macs}")
```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/7b4253d9-9f40-48c5-a773-0e7c4696e340.png)


### 2-1-5. Use forward hooks to extract the output activations of  the Conv2d layers.
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
import torchvision.models as models
import torch

activation = {}
# Define a hook function
def get_activation(name):
    def hook(model, input, output):
        activation[name] = output.detach()
    return hook

# Load a pre-trained googlenet model
# model = models.googlenet(pretrained=True)
model.eval()

# Define a hook function
def get_activation(name):
    def hook(model, input, output):
        activation[name] = output.detach()
    return hook


# Dictionary to store activations from each layer
activation = {}

# Register hook to each linear layer
for layer_name, layer in model.named_modules():
    if isinstance(layer, torch.nn.Conv2d):
        # Register forward hook
        layer.register_forward_hook(get_activation(layer_name))

# Run model inference
data = torch.randn(1, 3, 224, 224)
output = model(data)

# Access the saved activations
for layer in activation:
    print(f"Activation from layer {layer}: {activation[layer].shape}")
```
![](https://course.playlab.tw/md/uploads/29a845d1-95a6-45c6-bcb3-7396c17060b9.png)


#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::

## HW 2-2 Add more statistics to analyze the an ONNX model Using sclblonnx

### 2-2-1. model characteristics
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
!wget https://github.com/onnx/models/raw/main/validated/vision/classification/mobilenet/model/mobilenetv2-10.onnx
```
```python=
import onnx

onnx_model = onnx.load('./mobilenetv2-10.onnx')

# The model is represented as a protobuf structure and it can be accessed
# using the standard python-for-protobuf methods

## list all the operator types in the model
node_list = []
count = []
for i in onnx_model.graph.node:
    if (i.op_type not in node_list):
        node_list.append(i.op_type)
        count.append(1)
    else:
        idx = node_list.index(i.op_type)
        count[idx] = count[idx]+1
print(node_list)
print(count)

```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/b62a7244-3ad6-44e3-b713-9e16d240c304.png)


### 2-2-2. Data bandwidth requirement 
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
!pip install tabulate
```
```python=
import onnx
from onnx import shape_inference
from os import path
import sys
from tabulate import tabulate
from onnx import onnx_ml_pb2 as xpb2


onnx_model = onnx.load("mobilenetv2-10.onnx", load_external_data=False)
onnx.checker.check_model(onnx_model)

inferred_model = shape_inference.infer_shapes(onnx_model)
print('shape inference complete ...')

def _parse_element(elem: xpb2.ValueInfoProto):
    name = getattr(elem, 'name', "None")
    data_type = "NA"
    shape_str = "NA"
    etype = getattr(elem, 'type', False)
    if etype:
        ttype = getattr(etype, 'tensor_type', False)
        if ttype:
            data_type = getattr(ttype, 'elem_type', 0)
            shape = getattr(elem.type.tensor_type, "shape", False)
            if shape:
                shape_str = "["
                dims = getattr(shape, 'dim', [])
                for dim in dims:
                    vals = getattr(dim, 'dim_value', "?")
                    shape_str += (str(vals) + ",")
                shape_str = shape_str.rstrip(",")
                shape_str += "]"
    return name, data_type, shape_str

def get_valueproto_or_tensorproto_by_name(name: str, graph: xpb2.GraphProto):
    for i, node in enumerate(inferred_model.graph.node):
            if node.name == "":
                inferred_model.graph.node[i].name = str(i)
    input_nlist = [k.name for k in graph.input]
    initializer_nlist = [k.name for k in graph.initializer]
    value_info_nlist = [k.name for k in graph.value_info]
    output_nlist = [k.name for k in graph.output]

    # get tensor data
    if name in input_nlist:
        idx = input_nlist.index(name)
        return graph.input[idx], int(1)
    elif name in value_info_nlist:
        idx = value_info_nlist.index(name)
        return graph.value_info[idx], int(2)
    elif name in initializer_nlist:
        idx = initializer_nlist.index(name)
        return graph.initializer[idx], int(3)
    elif name in output_nlist:
        idx = output_nlist.index(name)
        return graph.output[idx], int(4)
    else:
        print("[ERROR MASSAGE] Can't find the tensor: ", name)
        print('input_nlist:\n', input_nlist)
        print('===================')
        print('value_info_nlist:\n', value_info_nlist)
        print('===================')
        print('initializer_nlist:\n', initializer_nlist)
        print('===================')
        print('output_nlist:\n', output_nlist)
        print('===================')
        return False, 0

def cal_tensor_mem_size(elem_type: str, shape: [int]):
    mem_size = int(1)
    # traverse the list to get the number of the elements
    for num in shape:
        mem_size *= num
    if elem_type == 1:   # "FLOAT": 1,
        mem_size *= 4
    elif elem_type == 2: # "UINT8": 2,
        mem_size *= 1
    elif elem_type == 3: # "INT8": 3,
        mem_size *= 1
    elif elem_type == 4: # "UINT16": 4,
        mem_size *= 2
    elif elem_type == 5: # "INT16": 5,
        mem_size *= 2
    elif elem_type == 6: # "INT32": 6,
        mem_size *= 4
    elif elem_type == 7: # "INT64": 7,
        mem_size *= 8
    elif elem_type == 9: # "BOOL": 9,
        mem_size *= 1
    elif elem_type == 10:# "FLOAT16": 10,
        mem_size *= 2
    elif elem_type == 11:# "DOUBLE": 11,
        mem_size *= 8
    elif elem_type == 12:# "UINT32": 12,
        mem_size *= 4
    elif elem_type == 13:# "UINT64": 13,
        mem_size *= 8
    elif elem_type == 14:# "COMPLEX64": 14,
        mem_size *= 8
    elif elem_type == 15:# "COMPLEX128": 15
        mem_size *= 16
    else:
        print("Undefined data type")

    return mem_size


def get_bandwidth(graph: xpb2.GraphProto):
    try:
        mem_BW_list = []
        total_mem_BW = 0
        unknown_tensor_list = []
        # traverse all the nodes
        for nodeProto in graph.node:
            # init variables
            read_mem_BW_each_layer = 0
            write_mem_BW_each_layer = 0
            total_each_layer = 0
            # traverse all input tensor
            for input_name in nodeProto.input:
                # get the TensorProto/ValueInfoProto by searching its name
                proto, type_Num = get_valueproto_or_tensorproto_by_name(input_name, graph)
                # parse the ValueInfoProto/TensorProto
                if proto:
                    if type_Num == 3:
                        dtype = getattr(proto, 'data_type', False)
                        # get the shape of the tensor
                        shape = getattr(proto, 'dims', [])
                    elif type_Num == 1 or type_Num == 2:
                        name, dtype, shape_str = _parse_element(proto)
                        shape_str = shape_str.strip('[]')
                        shape_str = shape_str.split(',')
                        shape = []
                        for dim in shape_str:
                            shape.append(0) if dim=='' else shape.append(int(dim)) 
                    else:
                        print(
                            '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                            input_name, ' is from a wrong list !')
                else:
                    print(
                        '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                        input_name, ' is no found !')
                    unknown_tensor_list.append((nodeProto.name, input_name, nodeProto.op_type))
                # calculate the tensor size in btye
                read_mem_BW_each_layer += cal_tensor_mem_size(dtype, shape)

            # traverse all output tensor
            for output_name in nodeProto.output:
                # get the TensorProto/ValueInfoProto by searching its name
                proto, type_Num = get_valueproto_or_tensorproto_by_name(output_name, graph)
                # parse the ValueInfoProto
                if proto:
                    if type_Num == 2 or type_Num == 4:
                        # name, dtype, shape = utils._parse_ValueInfoProto(proto)
                        name, dtype, shape_str = _parse_element(proto)
                        shape_str = shape_str.strip('[]')
                        shape_str = shape_str.split(',')
                        shape = []
                        for dim in shape_str:
                            shape.append(0) if dim=='' else shape.append(int(dim)) 
                    else:
                        print(
                            '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                            output_name, ' is from a wrong list !')
                else:
                    print(
                        '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                        input_name, ' is no found !')
                    unknown_tensor_list.append((nodeProto.name, output_name, nodeProto.op_type))
                # calculate the tensor size in btye
                write_mem_BW_each_layer += cal_tensor_mem_size(dtype, shape)

            # cal total bw
            total_each_layer = read_mem_BW_each_layer + write_mem_BW_each_layer
            # store into tuple
            temp_tuple = (nodeProto.name, read_mem_BW_each_layer, write_mem_BW_each_layer, total_each_layer)
            #append it
            mem_BW_list.append(temp_tuple)
            # accmulate the value
            total_mem_BW += total_each_layer

        # display the mem_bw of eahc layer
        columns = ['layer', 'read_bw', 'write_bw', 'total_bw']
        # resort the list
        mem_BW_list = sorted(mem_BW_list, key=lambda Layer: Layer[1], reverse=True)
        print(tabulate(mem_BW_list, headers=columns))
        print('====================================================================================\n')
        # display it
        print("The memory bandwidth for processor to execute a whole model without on-chip-buffer is: \n",
            total_mem_BW, '(bytes)\n', float(total_mem_BW) / float(1000000), '(MB)\n')
        # display the unknown tensor
        columns = ['op_name', 'unfound_tensor', 'op_type']
        print(tabulate(unknown_tensor_list, headers=columns))
        print('====================================================================================\n')
    except Exception as e:
        print("[ERROR MASSAGE] Unable to display: " + str(e))
        return False

    return True

print("start")
get_bandwidth(inferred_model.graph)

```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/dd7f7439-f4d2-408a-9062-6cd364849eea.png)


### 2-2-3. activation memory storage requirement
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
import torchvision.models as models
import torch
activation = {}
# Define a hook function
def get_activation(name):
    def hook(model, input, output):
        activation[name] = output.detach()
    return hook

# Compute the memory size of output activation
def sizeOf_activation(tensor):
    return tensor.numel() * tensor.element_size()

# Load a pre-trained mobilenet_v2 model
model = models.mobilenet_v2(pretrained=True)
model.eval()

# Register hook to each layer
for layer_name, layer in model.named_modules():
        # Register forward hook
        layer.register_forward_hook(get_activation(layer_name))

# Run model inference
data = torch.randn(1, 3, 224, 224)
output = model(data)

# Accumulate the memory usage among all output activation of layers
total_mem = 0
for act_name in activation:
    mem = sizeOf_activation(activation[act_name])
    total_mem += mem
    print(f"Activation from layer '{act_name:<20}' with {str(activation[act_name].shape):<30} costs {mem} byte(s)")

print()
print(f"{total_mem} bytes ({total_mem/(1024**2):.2f} MB) local memory storage is required to keep whole activations.")

```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::

|||
|---|---|
|![](https://course.playlab.tw/md/uploads/505200b9-ca97-4bad-9634-20118b920433.png)|![](https://course.playlab.tw/md/uploads/b78860f2-d489-49ea-9b90-170d04f67644.png)|

## HW 2-3 Build tool scripts to manipulate an ONNX model graph

### 2-3-1. create a subgraph (1) that consist of a single Linear layer of size MxKxN

#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
import torch
import torch.nn as nn



# Model definition
class Linear(nn.Module):
    def __init__(self):
        super(Linear, self).__init__()
    
    def forward(self, matA, matB):
        matC = torch.matmul(matA, matB)
        return matC
        

M, K, N = 128, 128, 128
matA = torch.randn(M, K)
matB = torch.randn(K, N)
model = Linear()
matC = model(matA, matB)


# Export the model to an ONNX file
torch.onnx.export(model, 
                  (matA, matB), 
                  "hw2-3-1.onnx", 
                  opset_version=11, 
                  do_constant_folding=True, 
                  input_names=['A', 'B'], 
                  output_names=['C']
                  )
```
#### Visualize the subgraph (1)
:::info
Please use netron to visualize the subgraph (1) that you generate. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/c2b977a8-90ef-4e54-aa60-fefedb0aaa02.png)



### 2-3-2. create a subgraph (2) as shown in the above diagram for the subgraph (1)  

#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
import torch
import torch.nn as nn
import torch.nn.functional as F
import math

# Model definition
class TiledLinear(nn.Module):
    def __init__(self, M, K, N, tiledSize=64):
        super(TiledLinear, self).__init__()
        self.M, self.K, self.N = M, K, N
        self.ts = tiledSize

    def forward(self, matA, matB):
        M, K, N = self.M, self.K, self.N
        Ahblk = math.ceil(K/tiledSize)
        Avblk = math.ceil(M/tiledSize)
        Bhblk = math.ceil(N/tiledSize)
        Bvblk = math.ceil(K/tiledSize)
        Chblk = Bhblk
        Cvblk = Avblk
        ts = self.ts
    
        Ahpad = (Ahblk*ts) - K
        Avpad = (Avblk*ts) - M
        Bhpad = (Bhblk*ts) - N
        Bvpad = (Bvblk*ts) - K
        
        paddedA = F.pad(matA, (0, Ahpad, 0, Avpad)) if Ahpad>0 or Avpad>0 else matA
        paddedB = F.pad(matB, (0, Bhpad, 0, Bvpad)) if Bhpad>0 or Bvpad>0 else matB

        rowtiledA = torch.split(paddedA, ts, dim=0)
        rowtiledB = torch.split(paddedB, ts, dim=0)
        
        tiledA = [torch.split(chunk, ts, dim=1) for chunk in rowtiledA]
        tiledB = [torch.split(chunk, ts, dim=1) for chunk in rowtiledB]
        tiledC = [[[torch.zeros(ts, ts) for _ in range(Bhblk)] for _ in range(Ahblk)] for _ in range(Avblk)]

        # MM(i, j, k) = matA(i, j) * matB(j, k)
        for i in range (Avblk):
            for j in range(Ahblk):
                for k in range(Bhblk):
                    tiledC[i][j][k] = torch.matmul(tiledA[i][j], tiledB[j][k])

        # Hard code in this hw specification
        # C00=A00∗B00+A01∗B10
        # C01=A00∗B01+A01∗B11
        # C10=A10∗B00+A11∗B10
        # C11=A10∗B01+A11∗B11
        
        C00 = tiledC[0][0][0]+tiledC[0][1][0]
        C01 = tiledC[0][0][1]+tiledC[0][1][1] 
        C10 = tiledC[1][0][0]+tiledC[1][1][0] 
        C11 = tiledC[1][0][1]+tiledC[1][1][1] 
        
        catC = torch.cat((torch.cat((C00, C01), dim=1), torch.cat((C10, C11), dim=1)), dim=0)
                
        matC = catC[:M, :N] if Avpad>0 or Bhpad>0 else catC
        
        return matC
        


# According to the parameters of first linear layer in Alexnet
M, K, N, tiledSize = 128, 128, 128, 64
model = TiledLinear(M, K, N, tiledSize=tiledSize)
matA = torch.randn(M, K)
matB = torch.randn(K, N)
model(matA, matB)

# Export the model to an ONNX file
torch.onnx.export(model, 
                  (matA, matB),
                  "hw2-3-2.onnx", 
                  opset_version=11, 
                  do_constant_folding=True, 
                  input_names=['A', 'B'], 
                  output_names=['C'])
```

#### Visualize the subgraph (2)
:::info
Please use netron to visualize the subgraph (2) that you generate. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/ad3c5ac6-7765-4ec0-b199-d3da1f43320b.png)



### 2-3-3. replace the `Linear` layers in the AlexNet with the equivalent subgraphs (2)
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
import torch
import torchvision.models as models
import torch.nn as nn
import torch.nn.functional as F
import math

device = 'cuda' if torch.cuda.is_available() else 'cpu'
print(device)
        
class TiledLinear_slice(nn.Module):
    def __init__(self, M, K, N, weight, bias, tiledSize=64):
        super(TiledLinear_slice, self).__init__()
        self.M, self.K, self.N = M, K, N
        self.weight = weight
        self.ts = tiledSize
        self.bias = bias

    # MM(i, j, k) = matA(i, j) * matB(j, k)
    def forward(self, matA):
        
        M, K, N = self.M, self.K, self.N
        matB = self.weight.t()
        bias = self.bias
        ts = self.ts
        Ahblk = math.ceil(K/ts)
        Avblk = math.ceil(M/ts)
        Bhblk = math.ceil(N/ts)
        Bvblk = math.ceil(K/ts)
        Chblk = Bhblk
        Cvblk = Avblk
    
        Ahpad = (Ahblk*ts) - K
        Avpad = (Avblk*ts) - M
        Bhpad = (Bhblk*ts) - N
        Bvpad = (Bvblk*ts) - K
        
        paddedA = F.pad(matA, pad=(0, Ahpad, 0, Avpad), mode='constant', value=0) if Ahpad>0 or Avpad>0 else matA
        paddedB = F.pad(matB, pad=(0, Bhpad, 0, Bvpad), mode='constant', value=0) if Bhpad>0 or Bvpad>0 else matB

        tiledA = [[paddedA[i*ts:(i+1)*ts, j*ts:(j+1)*ts] for j in range(Ahblk)] for i in range(Avblk)] 
        tiledB = [[paddedB[i*ts:(i+1)*ts, j*ts:(j+1)*ts] for j in range(Bhblk)] for i in range(Bvblk)]
        tiledC = [[torch.Tensor(ts, ts).zero_() for _ in range(Chblk)] for _ in range(Cvblk)]

        # MM(i, j, k) = matA(i, j) * matB(j, k)
        for i in range (Avblk):
            for j in range(Ahblk):
                for k in range(Bhblk):
                    tiledC[i][k] += torch.matmul(tiledA[i][j], tiledB[j][k])
                    
        rowcatC = [torch.cat(row, dim=1) for row in tiledC]
        catC = torch.cat(rowcatC, dim=0)
                
        matC = catC[:M, :N] if Avpad>0 or Bhpad>0 else catC
        matC += bias
        
        return matC
        
  # (classifier): Sequential(
  #   (0): Dropout(p=0.5, inplace=False)
  #   (1): Linear(in_features=9216, out_features=4096, bias=True)
  #   (2): ReLU(inplace=True)
  #   (3): Dropout(p=0.5, inplace=False)
  #   (4): Linear(in_features=4096, out_features=4096, bias=True)
  #   (5): ReLU(inplace=True)
  #   (6): Linear(in_features=4096, out_features=1000, bias=True)
  # )
        
class ModifiedAlexNet_TiledLinear(nn.Module):
    def __init__(self, original_model, tiledSize):
        super(ModifiedAlexNet_TiledLinear, self).__init__()
        self.features = original_model.features
        self.avgpool = original_model.avgpool
        self.classifier = nn.Sequential(
            original_model.classifier[0],
            TiledLinear_slice(1, 9216, 4096, original_model.classifier[1].weight, original_model.classifier[1].bias, tiledSize),  # Replace the first Linear layer with the tiled version
            original_model.classifier[2],
            original_model.classifier[3],
            TiledLinear_slice(1, 4096, 4096, original_model.classifier[4].weight, original_model.classifier[4].bias, tiledSize),  # Replace the second Linear layer with the tiled version
            original_model.classifier[5],
            TiledLinear_slice(1, 4096, 1000, original_model.classifier[6].weight, original_model.classifier[6].bias, tiledSize)  # Replace the third Linear layer with the tiled version
        )
    def forward(self, x):
        x = self.features(x)
        x = self.avgpool(x)
        x = torch.flatten(x, 1)
        x = self.classifier(x)
        return x

M, K, N, tiledSize = 1, 9216, 4096, 512
original_model = models.alexnet(pretrained=True)
TiledLinear_model = ModifiedAlexNet_TiledLinear(original_model, tiledSize)
original_model.eval()
TiledLinear_model.eval()

input_data = torch.randn(1, 3, 224, 224)

# Export the model to an ONNX file
torch.onnx.export(TiledLinear_model, 
                  input_data, 
                  "hw2-3-3.onnx", 
                  opset_version=11, 
                  do_constant_folding=True, 
                  input_names=['input'], 
                  output_names=['output'])

# Prepared for hw2-4
torch.save(TiledLinear_model.state_dict(), '../hw2-4/modified_alexnet.pt')
model.eval()

input_data = torch.randn(1, 3, 224, 224)

# Use torch.jit.trace to generate a torch.jit.ScriptModule via tracing.
traced_script_module = torch.jit.trace(TiledLinear_model, input_data)
# Serializing Your Script Module to a File
traced_script_module.save("../hw2-4/traced_modified_alexnet.pt")
```

#### Visualize the transformed model graph
:::info
Please use netron to visualize the transformed model graph that you generate. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/f2f12297-0cfe-4593-a716-132691a307af.png)



### 2-3-4. Correctness Verification
#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```python=
input_data = torch.randn(1, 3, 224, 224)

with torch.no_grad():
    original_output = original_model(input_data)
    TiledLinear_output = TiledLinear_model(input_data)

mse_original_tiledlinear = torch.mean((original_output - TiledLinear_output)**2)
print(f"MSE between original and TiledLinear model: {mse_original_tiledlinear:.06}")
```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/41086b43-abc2-4225-9cf7-35a4655b4105.png)





## HW 2-4 Using Pytorch C++ API to do model analysis on the transformed model graph

### 2-4-1. Calculate memory requirements for storing the model weights.

#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::
```cpp=
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
```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/05640220-a1db-44fb-a005-c8c529b1483f.png)


### 2-4-2. Export A list of operators with input tensor shape and output tensor shape

#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::

```cpp=
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
```
#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/0e358035-9cd0-42e4-8bc2-e9245d71baeb.png)


### 2-4-3. Calculate memory requirements for storing the activations

#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::

```cpp
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
```

#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/2295360f-3f20-44f0-b275-dd792f5c2058.png)


### 2-4-4. Calculate computation requirements

#### Code
:::info
Please copy and paste your code in this section. Remember to write clear comment in your code to explain your implementation in more details.
:::

```cpp
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
```
#### Execution Result
:::info
Please compile and run the program. 
Take a screenshot of the execution result and paste it here. 
:::
![](https://course.playlab.tw/md/uploads/c8ad5f31-088f-4c80-9b83-d85bdf13bfbe.png)




### 2-4-5. Compare your results to the result in HW2-1 and HW2-2

#### Discussion
:::info
Please describe your findings and draw conclusion in this section
:::

Following pytorch results are from `hw2-3.ipynb`, section`Preparation for 2-4`, and previous libtorch results in section 2-4-1~2-4-4


##### 2-4-1. Calculate memory requirements for storing the model weights

```python=
param_size = sum(p.numel() * p.element_size() for p in TiledLinear_model.parameters())
print(f"Total memory for parameters: {param_size} byte")
```
|pytorch|libtorch|
|---|---|
|![](https://course.playlab.tw/md/uploads/1c254045-7c22-4b47-afee-5fde719b8a0b.png)|![](https://course.playlab.tw/md/uploads/5248c7fa-dd0b-44ea-8c5a-d42eefde07ee.png)|

The results of pytorch and libtorch are equivalent, both total memory for parameters are `244403360`.



##### 2-4-2. Export A list of operators with input tensor shape and output tensor shape
```python=
from torchinfo import summary

summary(model=TiledLinear_model, input_size=(1, 3, 224, 224), device='cpu')
```

|pytorch|libtorch|
|---|---|
|![](https://course.playlab.tw/md/uploads/c6e45859-185c-4ac3-9a7d-8c120cf94790.png)|![](https://course.playlab.tw/md/uploads/50a8e5d7-f039-49d5-90de-d717aa104e5c.png)|

The shape results of pytorch and libtorch are equivalent.

##### 2-4-3. Calculate memory requirements for storing the activations
```python=
from torchinfo import summary

summary(model=TiledLinear_model, input_size=(1, 3, 224, 224), device='cpu')
```

|pytorch|libtorch|
|---|---|
|![](https://course.playlab.tw/md/uploads/e189b8de-fcac-477f-847d-f6f01bc59721.png)|![](https://course.playlab.tw/md/uploads/ed05e7d2-5608-4617-8d5e-3e1ad7e382e0.png)|

The activation memory computed by pytorch is 3.95MB, while the libtorch result is 4429728bytes (4.22MB).

Since I don't know which layer output should be count in activation memory, I simply accumulated all output size of layers.
Which memory is slightly larger than result given by pytorch. 

##### 2-4-4. Calculate computation requirements
```python=
import torch
import torch.nn as nn
import math

def calculate_output_shape(input_shape, layer):
    # Calculate the output shape for Conv2d, MaxPool2d, and Linear layers
    if isinstance(layer, (nn.Conv2d, nn.MaxPool2d)):
        kernel_size = (
            layer.kernel_size
            if isinstance(layer.kernel_size, tuple)
            else (layer.kernel_size, layer.kernel_size)
        )
        stride = (
            layer.stride
            if isinstance(layer.stride, tuple)
            else (layer.stride, layer.stride)
        )
        padding = (
            layer.padding
            if isinstance(layer.padding, tuple)
            else (layer.padding, layer.padding)
        )
        dilation = (
            layer.dilation
            if isinstance(layer.dilation, tuple)
            else (layer.dilation, layer.dilation)
        )
        ceil_mode = False
        if isinstance(layer, nn.MaxPool2d):
            ceil_mode = layer.ceil_mode

        if(ceil_mode):
            output_height = math.ceil((
                input_shape[1] + 2 * padding[0] - dilation[0] * (kernel_size[0] - 1) - 1
            ) // stride[0] + 1)
            output_width = math.ceil((
                input_shape[2] + 2 * padding[1] - dilation[1] * (kernel_size[1] - 1) - 1
            ) // stride[1] + 1)
        else:
            output_height = math.floor((
                input_shape[1] + 2 * padding[0] - dilation[0] * (kernel_size[0] - 1) - 1
            ) // stride[0] + 1)
            output_width = math.floor((
                input_shape[2] + 2 * padding[1] - dilation[1] * (kernel_size[1] - 1) - 1
            ) // stride[1] + 1)
            
        return (
            layer.out_channels if hasattr(layer, "out_channels") else input_shape[0],
            output_height,
            output_width,
        )
    elif isinstance(layer, nn.Linear):
        # For Linear layers, the output shape is simply the layer's output features
        return (layer.out_features,)
    elif isinstance(layer, nn.BatchNorm2d):
        # For Linear layers, the output shape is simply the layer's output features
        return (layer.out_features,)
    else:
        return input_shape


def calculate_macs(layer, input_shape, output_shape):
    # Calculate MACs for Conv2d and Linear layers
    if isinstance(layer, nn.Conv2d):
        kernel_ops = (
            layer.kernel_size[0]
            * layer.kernel_size[1]
            * (layer.in_channels / layer.groups)
        )
        output_elements = output_shape[1] * output_shape[2]
        macs = int(kernel_ops * output_elements * layer.out_channels)
        return macs
    elif isinstance(layer, nn.Linear):
        # For Linear layers, MACs are the product of input features and output features
        macs = int(layer.in_features * layer.out_features)
        return macs
    elif isinstance(layer, TiledLinear_slice):
        macs = int(layer.K * layer.N)
        return macs
    else:
        return 0


input_shape = (3, 224, 224)
total_macs = 0

# Iterate through the layers of the model
for name, layer in TiledLinear_model.named_modules():
       
    if isinstance(layer, (nn.Conv2d, nn.MaxPool2d, nn.ReLU, nn.Linear, TiledLinear_slice)):
        output_shape = calculate_output_shape(input_shape, layer)
        macs = calculate_macs(layer, input_shape, output_shape)
        total_macs += macs
        if isinstance(layer, (nn.Conv2d, nn.Linear, TiledLinear_slice)):
            print(
                f"Layer: {name}, Type: {type(layer).__name__}, Input Shape: {input_shape}, Output Shape: {output_shape}, MACs: {macs}"
            )
        elif isinstance(layer, nn.MaxPool2d):
            # Also print shape transformation for MaxPool2d layers (no MACs calculated)
            print(
                f"Layer: {name}, Type: {type(layer).__name__}, Input Shape: {input_shape}, Output Shape: {output_shape}, MACs: N/A"
            )
        input_shape = output_shape  # Update the input shape for the next layer

print(f"Total MACs: {total_macs}")
```

|pytorch|libtorch|
|---|---|
|![](https://course.playlab.tw/md/uploads/7d7043c7-3155-4249-9e7e-0828b3a1bb91.png)|![](https://course.playlab.tw/md/uploads/d9a17567-a28a-481e-96e8-de072a4ce5fd.png)|

The mac results of pytorch and libtorch are equivalent.







## Others
- If you have any comment or recommendation to this lab, you can write it down here to tell us.


