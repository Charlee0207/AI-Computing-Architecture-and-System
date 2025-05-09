//main.c
#include <stdio.h>
#include <stdlib.h>
#include "sudoku_2x2_c.h"
#define SIZE 16

// Task 3-1
// char test_c_data[16] = { 0, 3, 0, 0, 
//                          0, 0, 3, 0,
//                          2, 0, 0, 1, 
//                          0, 0, 0, 3 };
                      
// char test_asm_data[16] = { 0, 3, 0, 0, 
//                            0, 0, 3, 0,
//                            2, 0, 0, 1, 
//                            0, 0, 0, 3 };

// Task 3-2
char test_c_data[16] = { 1, 2, 0, 0, 
                         0, 0, 0, 1,
                         2, 0, 0, 0, 
                         0, 0, 3, 0 };
                      
char test_asm_data[16] = { 1, 2, 0, 0, 
                           0, 0, 0, 1,
                           2, 0, 0, 0, 
                           0, 0, 3, 0 };

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

    flag = 0;
    for( i=0 ; i<SIZE ; i++) {
        if (*(test_c_data+i)==0) {
            flag = 1;
            break;
        }
    }
    if (flag == 1){
        puts("\n\nand the problem is not valid!\nno solution\n");
    }
    else {
        puts("\n\nand the problem is valid\n");
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