#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include "maxpool2D.h"


void maxpool2D_c(const int8_t *input_X, int32_t input_X_dimW, int32_t input_X_dimH, int32_t input_X_dimC, int8_t *output_Y, int32_t kernel_W, int32_t kernel_H, int32_t stride_W, int32_t stride_H);
void maxpool2D_asm(const int8_t *input_X, int32_t input_X_dimW, int32_t input_X_dimH, int32_t input_X_dimC, int8_t *output_Y, int32_t kernel_W, int32_t kernel_H, int32_t stride_W, int32_t stride_H);

void print_result(const int8_t *golden_Y, const int8_t *output_Y_c, const int8_t *output_Y_asm, const int32_t output_Y_size){
    char str[10];
    int i;

    puts("Golden output_Y:\t[");
    for(i=0; i<output_Y_size; i++){
        int j = (int32_t)(*(golden_Y+i));
        itoa(j, str, 10);
        puts(str);
        if(i==output_Y_size-1)
            puts("]\n");
        else
            puts(", ");
    }

    puts("C output_Y:\t\t[");
    for(i=0; i<output_Y_size; i++){
        int j = (int32_t)(*(output_Y_c+i));
        itoa(j, str, 10);
        puts(str);
        if(i==output_Y_size-1)
            puts("]\n");
        else
            puts(", ");
    }

    puts("ASM output_Y:\t\t[");
    for(i=0; i<output_Y_size; i++){
        int j = (int32_t)(*(output_Y_asm+i));
        itoa(j, str, 10);
        puts(str);
        if(i==output_Y_size-1)
            puts("]\n");
        else
            puts(", ");
    }
}

int check_correctness(const int32_t testcase, const int8_t *golden_Y, const int8_t *output_Y_c, const int8_t *output_Y_asm, const int32_t output_Y_size){
    int correct_c = memcmp(golden_Y, output_Y_c, output_Y_size);
    int correct_asm = memcmp(golden_Y, output_Y_asm, output_Y_size);

    char str[10];
    itoa(testcase, str, 10);
    puts("Testcase ");
    puts(str);
    puts("\t===>\t");

    if(correct_c==correct_asm && correct_c==0){
        puts("[PASS]\n");
        return 0;
    }
    else if(correct_c!=0 || correct_asm!=0){
        print_result(golden_Y, output_Y_c, output_Y_asm, output_Y_size);
        puts("[FAIL]\t");
        if(correct_c!=0)
            puts("[C] ");
        if(correct_asm!=0)
            puts("[ASM] ");

        puts("function get error result\n");
            
        return 1;
    }

}


int main(){
    int error = 0;
    char str[10];

    for(int idx=0; idx<testcase_count; idx++){

        maxpool2D_c(    input_X_list[idx], 
                        input_X_dimW_list[idx], 
                        input_X_dimH_list[idx], 
                        input_X_dimC_list[idx],
                        output_Y_c,
                        kernel_W_list[idx],
                        kernel_H_list[idx],
                        stride_W_list[idx],
                        stride_H_list[idx]);
                    
        maxpool2D_asm(  input_X_list[idx], 
                        input_X_dimW_list[idx], 
                        input_X_dimH_list[idx], 
                        input_X_dimC_list[idx],
                        output_Y_asm,
                        kernel_W_list[idx],
                        kernel_H_list[idx],
                        stride_W_list[idx],
                        stride_H_list[idx]);

        
        error += check_correctness(idx, golden_Y_list[idx], output_Y_c, output_Y_asm, output_Y_dimC_list[idx]*output_Y_dimH_list[idx]*output_Y_dimW_list[idx]);
    }
    
    puts("==========================================================================================\n");
    puts("Error count: ");
    itoa(error, str, 10);
    puts(str);
    puts("\n\n");

    
    return 0;
}