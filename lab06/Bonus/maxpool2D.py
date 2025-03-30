import torch
import torch.nn.functional as F
import numpy as np


def generate_test_case(input_channels, input_height, input_width, kernel_size, stride_size):

    input_tensor = torch.randint(-128, 128, (input_channels, input_height, input_width), dtype=torch.int8)

    output_tensor = F.max_pool2d(input_tensor, kernel_size, stride_size)

    input_np = input_tensor.numpy()
    output_np = output_tensor.numpy()

    return input_np, kernel_size, stride_size, output_np




def write_test_case_to_file(input_X_dimW_list, input_X_dimH_list, input_X_dimC_list, output_Y_dimW_list, output_Y_dimH_list, output_Y_dimC_list, kernel_W_list, kernel_H_list, stride_W_list, stride_H_list, input_X_list, golden_Y_list, output_Y_size, filename="maxpool2D.h"):
    with open(filename, "w") as f:

        assert(len(input_X_list)==len(golden_Y_list))
        testcase_count = len(input_X_list)

        f.write("#include <stdint.h>\n\n")
        f.write(f"int8_t output_Y_c[{output_Y_size}];\n")
        f.write(f"int8_t output_Y_asm[{output_Y_size}];\n")
        f.write(f"int32_t testcase_count = {testcase_count};\n")
        f.write(f"int32_t input_X_dimW_list[{testcase_count}] = {{")
        for idx, val in enumerate(input_X_dimW_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t input_X_dimH_list[{testcase_count}] = {{")
        for idx, val in enumerate(input_X_dimH_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t input_X_dimC_list[{testcase_count}] = {{")
        for idx, val in enumerate(input_X_dimC_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t output_Y_dimW_list[{testcase_count}] = {{")
        for idx, val in enumerate(output_Y_dimW_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t output_Y_dimH_list[{testcase_count}] = {{")
        for idx, val in enumerate(output_Y_dimH_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t output_Y_dimC_list[{testcase_count}] = {{")
        for idx, val in enumerate(output_Y_dimC_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t kernel_W_list[{testcase_count}] = {{")
        for idx, val in enumerate(kernel_W_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t kernel_H_list[{testcase_count}] = {{")
        for idx, val in enumerate(kernel_H_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        f.write(f"int32_t stride_W_list[{testcase_count}] = {{")
        for idx, val in enumerate(stride_W_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")

        f.write(f"int32_t stride_H_list[{testcase_count}] = {{")
        for idx, val in enumerate(stride_H_list):
            f.write(f"{val}, ") if idx!=testcase_count-1 else f.write(f"{val}}};\n")
            
        for idx, val in enumerate(input_X_list):
            input_X_size = val.shape[2]*val.shape[1]*val.shape[0]
            f.write(f"int8_t input_X_{idx}[{input_X_size}] = {{\\\n")
            f.write(np.array2string(val, separator=", ").replace("[", " ").replace("]", "") + "};\n")

        for idx, val in enumerate(golden_Y_list):
            golden_Y_size = val.shape[2]*val.shape[1]*val.shape[0]
            f.write(f"int8_t golden_Y_{idx}[{golden_Y_size}] = {{\\\n")
            f.write(np.array2string(val, separator=", ").replace("[", " ").replace("]", "") + "};\n")

        
        f.write(f"int8_t *input_X_list[{testcase_count}] = {{")
        for idx in range(testcase_count):
            f.write(f"input_X_{idx}, ") if idx!=testcase_count-1 else f.write(f"input_X_{idx}}};\n")
            
        f.write(f"int8_t *golden_Y_list[{testcase_count}] = {{")
        for idx in range(testcase_count):
            f.write(f"golden_Y_{idx}, ") if idx!=testcase_count-1 else f.write(f"golden_Y_{idx}}};\n")



if __name__ == "__main__":
    input_X_dimW_list = []
    input_X_dimH_list = []
    input_X_dimC_list = []
    output_Y_dimW_list = []
    output_Y_dimH_list = []
    output_Y_dimC_list = []
    kernel_W_list = []
    kernel_H_list = []
    stride_W_list = []
    stride_H_list = []
    input_X_list = []
    golden_Y_list = []
    output_Y_size = 0

    
    testcase_count = 0
    for input_channels in range(1, 5):  # Vary input channels from 1 to 4
        for input_height in range(4, 10):  # Vary input height from 4 to 9
            for input_width in range(4, 10):  # Vary input width from 4 to 9
                for kernel_size in [(2, 2), (2, 3), (2, 4), (3, 2), (3, 3), (3, 4), (4, 2), (4, 3), (4, 4)]: 
                    for stride_size in [(1, 1), (1, 2), (2, 1), (2, 2)]: 
                        testcase_count += 1 

                        input_tensor, kernel_size, stride_size, output_tensor = generate_test_case(input_channels, input_height, input_width, kernel_size, stride_size)

                        input_X_dimW_list.append(input_tensor.shape[2])
                        input_X_dimH_list.append(input_tensor.shape[1])
                        input_X_dimC_list.append(input_tensor.shape[0])
                        output_Y_dimW_list.append(output_tensor.shape[2])
                        output_Y_dimH_list.append(output_tensor.shape[1])
                        output_Y_dimC_list.append(output_tensor.shape[0])
                        kernel_W_list.append(kernel_size[1])
                        kernel_H_list.append(kernel_size[0])
                        stride_W_list.append(stride_size[1])
                        stride_H_list.append(stride_size[0])
                        input_X_list.append(input_tensor)
                        golden_Y_list.append(output_tensor)
                        output_Y_size_tmp = output_tensor.shape[2]*output_tensor.shape[1]*output_tensor.shape[0]
                        output_Y_size =  output_Y_size_tmp if output_Y_size_tmp>output_Y_size else output_Y_size

                        
    write_test_case_to_file(input_X_dimW_list, input_X_dimH_list, input_X_dimC_list, output_Y_dimW_list, output_Y_dimH_list, output_Y_dimC_list, kernel_W_list, kernel_H_list, stride_W_list, stride_H_list, input_X_list, golden_Y_list, output_Y_size, filename="maxpool2D.h")





