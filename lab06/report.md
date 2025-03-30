(NTHU_113062559_李承澔) ACAL 2024 Fall Lab 6 HW Submission Template
===


###### tags: `AIAS Fall 2024` `AIAS Spring 2024` `Submission Template`


## Gitlab code link

- [Gitlab link](https://course.playlab.tw/git/chngh0207/lab06) - 

## HW6-1 - Fibonacci Series
### Assembly Code
> 請放上你的程式碼並加上註解，讓 TA明白你是如何完成的。
```mipsasm=
## fibonacci.S
## put input n in register x10 (a0)  
## put output fibonacci(n) in register x11 (a1)
## use Venus to test correctness

.text
main:
## write assembly code here.
## call fibonacci function and get return value.
    li      a0, 16
    jal     fibonacci
    li		a0, 1                   # a0=1	print_int	prints integer in a1
    ecall
    li		a0, 10                  # a0=10	exit	    ends the program
    ecall

fibonacci:
## fibonacci function

prologue:
    addi    sp,sp,-16
    sw      ra,0(sp)
    sw      s0,4(sp)
    sw      s1,8(sp)
    sw      s2,12(sp)

funct:
    mv      s0, a0                  # move n into s0
    li      t1, 1
    beq     s0, zero, equal_zero    # if(n==0)
    beq     s0, t1, equal_one       # else if(n==1)

    # Recusrively call fibonacci(n-1) adn fibonacci(n-2)
    addi    a0, s0, -1              # a0 = n-1
    jal     fibonacci               # fibonacci(n-1)
    mv      s1, a1                  # store current fibonacci(n-1) in s1
    
    addi    a0, s0, -2              # a0 = n-2
    jal     fibonacci               # fibonacci(n-2)
    mv      s2, a1                  # store current fibonacci(n-2) in s2

    add     a1, s1, s2              # return fibonacci(n-1)+fibonacci(n-2)
    j       epilogue

equal_zero:
    li      a1, 0
    j       epilogue

equal_one:
    li      a1, 1
    j       epilogue

epilogue:
    lw      ra,0(sp)
    lw      s0,4(sp)
    lw      s1,8(sp)
    lw      s2,12(sp)
    addi    sp,sp,16
    jr      ra

```
### Simulation Result
> 請放上你在 Venus上的模擬結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/16320387-535b-4323-b40d-0fd099057ac9.png)


## HW6-2 - Fibonacci Series with C/Assembly Hybrid 
### Assembly Code & C Code
> 請放上你的程式碼並加上註解，讓 TA明白你是如何完成的。
- `fibonacci.S`
```mipsasm=
## fibonacci.S

    .text                          # code section 
    .global fibonacci_asm          # declar the sum_asm function as a  global function
    .type fibonacci_asm, @function # define sum_asm as a function 
fibonacci_asm:

    # Write your assembly code funtion here.
    # Please notice the rules of calling convention.

    addi    sp,sp,-4
    sw      ra,0(sp)

    jal     fibonacci_main
    mv      a0, a1                  # move the return value to a0
    
    lw      ra,0(sp)                # restore context
    addi    sp,sp,4
    jr      ra

fibonacci_main:

prologue:
    addi    sp,sp,-16
    sw      ra,0(sp)
    sw      s0,4(sp)
    sw      s1,8(sp)
    sw      s2,12(sp)

funct:
    mv      s0, a0                  # move n into s0
    li      t1, 1
    beq     s0, zero, equal_zero    # if(n==0)
    beq     s0, t1, equal_one       # else if(n==1)

    # Recusrively call fibonacci(n-1) adn fibonacci(n-2)
    addi    a0, s0, -1              # a0 = n-1
    jal     fibonacci_asm           # fibonacci(n-1)
    mv      s1, a1                  # store current fibonacci(n-1) in s1
    
    addi    a0, s0, -2              # a0 = n-2
    jal     fibonacci_asm           # fibonacci(n-2)
    mv      s2, a1                  # store current fibonacci(n-2) in s2

    add     a1, s1, s2              # return fibonacci(n-1)+fibonacci(n-2)
    j       epilogue

equal_zero:
    li      a1, 0
    j       epilogue

equal_one:
    li      a1, 1
    j       epilogue

epilogue:
    lw      ra,0(sp)
    lw      s0,4(sp)
    lw      s1,8(sp)
    lw      s2,12(sp)
    addi    sp,sp,16
    jr      ra
    
    .size fibonacci_asm, .-fibonacci_asm
```

- `fibonacci.c`
```cpp=
// HW2 main function
#include <stdio.h>
#include <stdlib.h>


int fibonacci_c(int n) { 
    if(n == 0) {
        return 0;
    }
        
    else if(n == 1) {
        return 1;
    }
        
    else {
        return fibonacci_c(n-1)+fibonacci_c(n-2);        
    }
}

int fibonacci_asm(int n);

int main() {
    int n = 6;    // setup input value n
    int out = 0; // setup output value fibonacci(n)

    out = fibonacci_c(n);
    char str[25];
    itoa(out,str,10);
    puts("C code fibonacci_c=");
    puts(str);
    puts("\n");  
    out = fibonacci_asm(n);
    puts("ASM code fibonacci_asm=");
    itoa(out,str,10);
    puts(str);
    puts("\n");
    return 0;
}
```

### Simulation Result
> 請放上你在 docker中的 qemu模擬結果，驗證程式碼的正確性。 (螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/62af2f96-11b7-48ed-b0df-6e1ca29597b6.png)


## HW6-3 - 2x2 Sudoku
### Assembly Code & C Code
> 請放上你的程式碼並加上註解，讓 TA明白你是如何完成的。
- `main.c`
```cpp=
//main.c
#include <stdio.h>
#include <stdlib.h>
#include "sudoku_2x2_c.h"
#define SIZE 16

char test_c_data[16] = { 0, 0, 2, 0, 
                         0, 0, 0, 4,
                         2, 3, 0, 0, 
                         0, 4, 0, 0 };
                      
char test_asm_data[16] = { 0, 0, 2, 0, 
                           0, 0, 0, 4,
                           2, 3, 0, 0, 
                           0, 4, 0, 0 };

void print_sudoku_result() {
    int i;
    char str[25];
    puts("Output c & assembly function result\n");
    puts("c result :\n");
    for( i=0 ; i<SIZE ; i++) {   
        int j= *(test_c_data+i);
        itoa(j, str, 10);
        puts(str);
    }

    puts("\n\nassembly result :\n");
    for( i=0 ; i<SIZE ; i++) {
        int j= *(test_asm_data+i);
        itoa(j, str, 10);
        puts(str);
    }

    int flag = 0;
    for( i=0 ; i<SIZE ; i++) {
        if (*(test_c_data+i) != *(test_asm_data+i)) {
            flag = 1;
            break;
        }
    }

    if (flag == 1){
        puts("\n\nyour c & assembly got different result ... QQ ...\n");
    }
    else {
        puts("\n\nyour c & assembly got same result!\n");
    }
}


void sudoku_2x2_asm(char *test_asm_data); // TODO, sudoku_2x2_asm.S

void sudoku_2x2_c(char *test_c_data); // TODO, sudoku_2x2_c.S
                        
int main() {
    sudoku_2x2_c(test_c_data);
    sudoku_2x2_asm(test_asm_data);
    print_sudoku_result();
    return 0;
}
```
- `sudoku_2x2_asm.S`
```mipsasm=
# sudoku_2x2_asm.S
#     .data
# test_asm_data:  
#     .byte 0, 0, 2, 0, 0, 0, 0, 4, 2, 3, 0, 0, 0, 4, 0, 0

    .text                           # code section 
    .global sudoku_2x2_asm          # declare the asm function as a global function
    .type sudoku_2x2_asm, @function # define sum_asm as a function 
# main:
#     la a0, test_asm_data
#     jal sudoku_2x2_asm
#     ecall
    
sudoku_2x2_asm:

    ## add your assembly code here

    addi    sp,sp,-4    # allocate stack for ra
    sw      ra,0(sp)

    li      a1, 0       # initial index
    mv      a2, a0      # store the pointer of char array into a2
    jal     solve   

    lw      ra,0(sp)    # restore context
    addi    sp,sp,4
    jr      ra

solve: 
    addi    sp,sp,-16   
    sw      ra,0(sp)
    sw      a1,4(sp)    # index
    sw      s0,8(sp)    # s0 used later
    sw      s1,12(sp)   # s0 used later

    ## if(index>=16)
    li      t0, 16
    bge     a1, t0, solve_return_true

    ## if(grid[index]!=0)
    add     s0, a1, a2  # grid+offset, since this is char array, we don't multiply index offset by 4
    lb      t1, 0(s0)   # load grid[index] into t1
    beq     t1, zero, solve_else # branch to else condition

    ## return solve(index+1)
    addi    sp,sp,-4    # backup current a1 = index
    sw      a1,0(sp)
    addi    a1, a1, 1
    jal     solve
    lw      a1,0(sp)
    addi    sp,sp,4     # restore current a1 = index
    j       solve_return

solve_else:
    ## else
    ## for(int n=1; n<=4; n++)
    li      s1, 1       # start with n=1

solve_loop:
    ## for(int n=1; n<=4; n++)
    li      t4, 4
    ble     s1, t4, solve_set_grid  # loop from 1 to 4
    j       solve_backtrack         # exit

solve_set_grid:
    ## grid[index] = n;
    sb      s1, 0(s0)   

    ## check(index)
    jal     check

    ## check(index)==TRUE
    li      t3, 1
    bne     a0, t3, solve_increment

    ## solve(index+1)
    addi    sp,sp,-4    # backup current a1 = index
    sw      a1,0(sp)
    addi    a1, a1, 1
    jal     solve
    lw      a1,0(sp)
    addi    sp,sp,4     # restore current a1 = index

    ## solve(index+1)==TRUE
    li      t3, 1
    bne     a0, t3, solve_increment
    j       solve_return_true
    
solve_increment:
    addi    s1, s1, 1   # n++
    j       solve_loop

solve_backtrack:
    ## grid[index] = 0;
    sb      zero, 0(s0)
    j       solve_return_false
    
solve_next:
    ## solve(index+1)
    addi    a1, a1, 1
    jal     solve

solve_return:
    j       solve_restore

solve_return_true:
    li      a0, 1
    j       solve_restore

solve_return_false:
    li      a0, 0
    j       solve_restore

solve_restore:    
    lw      ra,0(sp)
    lw      a1,4(sp)
    lw      s0,8(sp)
    lw      s1,12(sp)
    addi    sp,sp,16
    jr      ra



check:
    addi    sp,sp,-16   
    sw      ra,0(sp)
    sw      s0,4(sp)
    sw      s1,8(sp)
    sw      s2,12(sp)

    ## calculate row, col and num
    li      t4, 2
    srl     t0, a1, t4  # row = index/4
    li      t4, 4
    rem     t1, a1, t4  # col = index%4  
    add     s0, a1, a2  # grid+offset, since this is char array, we don't multiply index offset by 4
    lb      t2, 0(s0)   # num = grid[index]

    ## t0 -> row
    ## t1 -> col
    ## t2 -> num
    ## a2 -> grid[]

    ## row check
check_row:
    li      t4, 0               # c=0
check_row_loop:
    li      s0, 4
    bge     t4, s0, check_col   # c<4
    beq     t4, t1, next_row    # c==col, continue
    slli    t5, t0, 2           # row*4
    add     t5, t5, t4          # row*4+c
    add     t5, t5, a2          # grid[row*4+c]
    lb      t5, 0(t5)           # load grid[row*4+c]
    beq     t5, t2, check_return_false
next_row:
    addi    t4, t4, 1           # c++
    j check_row_loop

    ## col check
check_col:
    li      t4, 0               # r=0
check_col_loop:
    li      s0, 4
    bge     t4, s0, check_subgrid# r<4
    beq     t4, t0, next_col    # r==row, continue
    slli    t5, t4, 2           # r*4
    add     t5, t5, t1          # r*4+col
    add     t5, t5, a2          # grid[row*4+c]
    lb      t5, 0(t5)           # load grid[row*4+c]
    beq     t5, t2, check_return_false
next_col:
    addi    t4, t4, 1           # r++
    j check_col_loop

    ## subgrid check
check_subgrid:
    srli    t3, t0, 1           # (row/2)
    slli    t3, t3, 1           # (row/2)*2
    srli    t4, t1, 1           # (col/2)
    slli    t4, t4, 1           # (col/2)*2
    mv      t5, t3              # r = startRow
    mv      t6, t4              # c = startCol
    addi    s0, t3, 2           # startRow + 2
    addi    s1, t4, 2           # startCol + 2
    ## t0 -> row
    ## t1 -> col
    ## t2 -> num
    ## a2 -> grid[]
    ## t3 -> startRow
    ## t4 -> startCol
    ## t5 -> r
    ## t6 -> c
    ## s0 -> startRow + 2
    ## s1 -> startCol + 2

outer_loop:
    bge     t5, s0, end_outer_loop  # exit if r>=startRow + 2
    mv      t6, t4              # c = startCol

inner_loop:
    bge     t6, s1, end_inner_loop  # exit if c>=startCol + 2

    ## (r != row || c != col)
    bne     t5, t0, check_grid   
    bne     t6, t1, check_grid
    j       next_inner_loop

check_grid:
    ## grid[r*4 + c] == num
    slli    s2, t5, 2           # r*4
    add     s2, s2, t6          # r*4 + c
    add     s2, s2, a2          # grid[r*4 + c]
    lb      s2, 0(s2)           # load grid[r*4 + c]
    beq     s2, t2, check_return_false  # grid[r*4 + c] == num

next_inner_loop:
    addi    t6, t6, 1           # c++
    j       inner_loop

end_inner_loop:
    addi    t5, t5, 1           # r++
    j       outer_loop

end_outer_loop:
    li      a0, 1
    j       check_restore
    
check_return_false:
    li      a0, 0
    j       check_restore
    
check_restore:
    lw      ra,0(sp)
    lw      s0,4(sp)
    lw      s1,8(sp)
    lw      s2,12(sp)
    addi    sp,sp,16
    jr      ra


    .size sudoku_2x2_asm, .-sudoku_2x2_asm
```

- `sudoku_2x2_c.c`
```cpp=
#include <stdio.h>
#include <stdlib.h>

typedef enum{
    FALSE = 0,
    TRUE = !FALSE
} BOOL;

char *grid;

BOOL check(int index){
    int row = index/4;
    int col = index%4;
    int num = grid[index];

    // Check the row for duplicates
    for(int c=0; c<4; c++){
        if(c!=col && grid[row*4+c]==num){
            return FALSE;
        }
    }
    // Check the col for duplicates
    for(int r=0; r<4; r++){
        if(r!=row && grid[r*4+col]==num){
            return FALSE;
        }
    }
    // Check the 2x2 subgrid for duplicates
    int startRow = (row/2)*2;
    int startCol = (col/2)*2;
    for (int r = startRow; r < startRow + 2; r++) {
        for (int c = startCol; c < startCol + 2; c++) {
            if ((r != row || c != col) && grid[r*4 + c] == num) {
                return FALSE; 
            }
        }
    }

    return TRUE;
}

BOOL solve(int index){
    if(index>=16){
        return TRUE;
    }

    if(grid[index]!=0){
        return solve(index+1);
    }
    else{
        for(int n=1; n<=4; n++){
            grid[index] = n;
            if(check(index)==TRUE && solve(index+1)==TRUE){
                return TRUE;
            }
        }
    }
    grid[index] = 0;
    return FALSE;
}


void sudoku_2x2_c(char *test_c_data){

// TODO
// Finish your sudoku algorithm in c language
    grid = test_c_data;
    solve(0);
    
}
```

### Simulation Result
> 請放上你在 docker中的 rv32emu模擬結果，驗證程式碼的正確性。 (螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/ef2e4a1d-08eb-4757-9420-f511368ea261.png)


## Bonus
### Step 1 C Implementation
> 請放上你C程式碼並加上註解，讓 TA明白你是如何完成的。

`maxpool2D_c.c`
```c=
void maxpool2D_c(
    const signed char *input_X,
    int input_X_dimW,
    int input_X_dimH,
    int input_X_dimC,
    signed char *output_Y,
    int kernel_W,
    int kernel_H,
    int stride_W,
    int stride_H
) {
    // Calculate output dimensions
    int output_W = (input_X_dimW - kernel_W) / stride_W + 1;
    int output_H = (input_X_dimH - kernel_H) / stride_H + 1;

    for (int c = 0; c < input_X_dimC; c++) {
        for (int oh = 0; oh < output_H; oh++) {
            for (int ow = 0; ow < output_W; ow++) {
                // Initialize max value to the smallest possible value
                signed char max_val = -128;

                // Apply the kernel to the corresponding region in the input
                for (int kh = 0; kh < kernel_H; kh++) {
                    for (int kw = 0; kw < kernel_W; kw++) {
                        int ih = oh * stride_H + kh;
                        int iw = ow * stride_W + kw;

                        // Calculate the input index
                        int input_idx = c * (input_X_dimW * input_X_dimH) + ih * input_X_dimW + iw;


                        // Update max value
                        if (input_X[input_idx] > max_val) {
                            max_val = input_X[input_idx];
                        }
                    }
                }

                // Calculate the output index and store the result
                int output_idx = c * (output_W * output_H) + oh * output_W + ow;
                output_Y[output_idx] = max_val;
            }
        }
    }
}
```
`maxpool2D.c` (main function)
```c=
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include "maxpool2D.h"


void maxpool2D_c(const int8_t *input_X, int32_t input_X_dimW, int32_t input_X_dimH, int32_t input_X_dimC, int8_t *output_Y, int32_t kernel_W, int32_t kernel_H, int32_t stride_W, int32_t stride_H);
void maxpool2D_asm(const int8_t *input_X, int32_t input_X_dimW, int32_t input_X_dimH, int32_t input_X_dimC, int8_t *output_Y, int32_t kernel_W, int32_t kernel_H, int32_t stride_W, int32_t stride_H);

void print_result(const int8_t *golden_Y, const int8_t *output_Y_c, const int8_t *output_Y_asm, const int32_t output_Y_size){
    ...
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
```
I store testcases in `input_X_list`, and other attributes in `input_X_dimW_list`, `input_X_dimH_list`,.etc.

And the result of C and asm function are stored to `output_Y_c` and `output_Y_asm`.
Then they are passed to `check_correctness` function to check correctness with `golden_Y_list`, which is the golden data generated by `torch.nn.functional.max_pool2d` in `maxpool2D.py`.

Since it seems there's no filesystem in qemu to read file, I declared all testcases in `maxpool2D.h` as variables.

![](https://course.playlab.tw/md/uploads/f2121cd8-5035-4cb7-9016-5f2208eb64bd.png)


### Step 2 Assembly Implementation
> 請放上你Assembly程式碼並加上註解，同時驗證程式碼的正確性。把驗證的結果截圖貼上

```assembly=
    .text                           # code section 
    .global maxpool2D_asm          # declare the asm function as a global function
    .type maxpool2D_asm, @function # define sum_asm as a function 

    
maxpool2D_asm:

    addi sp, sp, -64                # Allocate stack space
    sw ra, 60(sp)                   # Save return address
    sw s0, 56(sp)                   # Save s0
    sw s1, 52(sp)                   # Save s1
    sw s2, 48(sp)                   # Save s2
    sw s3, 44(sp)                   # Save s3
    sw s4, 40(sp)                   # Save s4
    sw s5, 36(sp)                   # Save s5
    sw s6, 32(sp)                   # Save s6
    sw s7, 28(sp)                   # Save s7
    sw s8, 24(sp)                   # Save s8
    sw s9, 20(sp)                   # Save s9
    sw s10, 16(sp)                  # Save s10
    sw s11, 12(sp)                  # Save s11

    # Load arguments
    mv s0, a0                       # a0 = input_X
    mv s1, a1                       # a1 = input_X_dimW
    mv s2, a2                       # a2 = input_X_dimH
    mv s3, a3                       # a3 = input_X_dimC
    mv s4, a4                       # a4 = output_Y
    mv s5, a5                       # a5 = kernel_W
    mv s6, a6                       # a6 = kernel_H
    mv s7, a7                       # a7 = stride_W
    lw s8, 64(sp)                   # a8 = stride_H

    # Compute output_W and output_H
    sub s9, s1, s5                  # input_X_dimW - kernel_W
    div s9, s9, s7                  # (input_X_dimW - kernel_W) / stride_W
    addi s9, s9, 1                  # s9 = output_W
    sub s10, s2, s6                 # input_X_dimH - kernel_H
    div s10, s10, s8                # (input_X_dimH - kernel_H) / stride_H
    addi s10, s10, 1                # s10 = output_H

    li t0, 0                        # t0 = c = 0;

loop_c:
    bge t0, s3, end_c               # if(c>=input_X_dimC) break;
    li t1, 0                        # t1 = oh = 0;

loop_oh:
    bge t1, s10, end_oh             # if(oh>=output_H) break;
    li t2, 0                        # t2 = ow = 0;

loop_ow:
    bge t2, s9, end_ow              # if(ow>=output_W) break;
    li s11, -128                    # s11 = max_val = -128
    li t3, 0                        # t3 = kh = 0 

loop_kh:
    bge t3, s6, end_kh              # if(kh>=kernel_H) break;
    li t4, 0                        # t4 = kw = 0 

loop_kw:
    bge t4, s5, end_kw              # if(kw>=kernel_W) break;
    
    mul t5, t1, s8                  # oh * stride_H
    add t5, t5, t3                  # t5 = ih
    mul t6, t2, s7                  # ow * stride_W
    add t6, t6, t4                  # t6 = iw

    mul a2, s1, s2                  # input_X_dimW * input_X_dimH
    mul a2, a2, t0                  # a2 = c * (input_X_dimW * input_X_dimH)
    mul a3, t5, s1                  # a3 = ih * input_X_dimW
    add a2, a2, a3                  # a2 = a2+a3 = c * (input_X_dimW * input_X_dimH) + ih * input_X_dimW
    add a2, a2, t6                  # a2 = input_idx
    add a2, s0, a2                  # a2 = &input_X[input_idx]
    lb a3, 0(a2)                    # a3 = input_X[input_idx]

    blt s11, a3, update_max         # if(input_X[input_idx] > max_val)
    j skip_update

update_max:
    mv s11, a3                      # max_val = input_X[input_idx]
skip_update:
    addi t4, t4, 1                  # kw++
    j loop_kw

end_kw:
    addi t3, t3, 1                  # kh++
    j loop_kh

end_kh:
    mul a2, s9, s10                 # output_W * output_H
    mul a2, a2, t0                  # a2 = c * (output_W * output_H)
    mul a3, t1, s9                  # a3 = oh * output_W
    add a2, a2, a3                  # a2 = a2+a3 = c * (output_W * output_H) + oh * output_W
    add a2, a2, t2                  # a2 = output_idx
    add a2, s4, a2                  # a2 = &output_Y[output_idx]
    sb s11, 0(a2)                   # output_Y[output_idx] = max_val

    addi t2, t2, 1                  # ow++
    j loop_ow

end_ow:
    addi t1, t1, 1                  # oh++
    j loop_oh

end_oh:
    addi t0, t0, 1                  # c++
    j loop_c 

end_c:
    lw ra, 60(sp)                   # Restore return address
    lw s0, 56(sp)                   # Restore s0
    lw s1, 52(sp)                   # Restore s1
    lw s2, 48(sp)                   # Restore s2
    lw s3, 44(sp)                   # Restore s3
    lw s4, 40(sp)                   # Restore s4
    lw s5, 36(sp)                   # Restore s5
    lw s6, 32(sp)                   # Restore s6
    lw s7, 28(sp)                   # Restore s7
    lw s8, 24(sp)                   # Restore s8
    lw s9, 20(sp)                   # Restore s9
    lw s10, 16(sp)                  # Restore s10
    lw s11, 12(sp)                  # Restore s11
    addi sp, sp, 64                 # Deallocate stack space
    jr ra                           # Return to caller 

    .size maxpool2D_asm, .-maxpool2D_asm
```
run with command
```
make run_all
```
![](https://course.playlab.tw/md/uploads/90d3da06-e378-4a07-83ea-aba4b2d62340.png)

### Step 3 Performance Optimization
我們用Instruction Count 來代表performance 的測量metric 的話, 你能怎麼優化你的Assmbly implementation呢？

First, attributes can be fetched from argument registers directly, rather then moving argument registers to saved registers(s0~s11).
To reduce the overhead of context saving and restoring of saved registers.

Second, the output index are evaluated in the inner loop with multiplication and addition.
```c=
for (int c = 0; c < input_X_dimC; c++) {
    for (int oh = 0; oh < output_H; oh++) {
        for (int ow = 0; ow < output_W; ow++) {

            ...

            int output_idx = c * (output_W * output_H) + oh * output_W + ow;
            output_Y[output_idx] = max_val;
        }
    }
}
```

output index calculation in assembly
```assembly=
mul a2, s9, s10                 # output_W * output_H
mul a2, a2, t0                  # a2 = c * (output_W * output_H)
mul a3, t1, s9                  # a3 = oh * output_W
add a2, a2, a3                  # a2 = a2+a3 = c * (output_W * output_H) + oh * output_W
add a2, a2, t2                  # a2 = output_idx
add a2, s4, a2                  # a2 = &output_Y[output_idx]
sb s11, 0(a2)                   # output_Y[output_idx] = max_val
```
However, the index can be initialize before entering loop, and update index by add 1 as ow updating. Which shrinks output index calculation to only one add instruction.

Finally, I only implement the version of first optimization above. Here's simulation result.

run with command
```
make run_all_opt
```
![](https://course.playlab.tw/md/uploads/610ad3ad-76c9-464d-bce1-38adaff8b380.png)


