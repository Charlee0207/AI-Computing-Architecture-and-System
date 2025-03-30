//main.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "puzzles.h"
#include "../sudoku_2x2_c.h"
#define SIZE 16

char test_c_data[16] = { 0, 0, 2, 0, 
                         0, 0, 0, 4,
                         2, 3, 0, 0, 
                         0, 4, 0, 0 };
                      
char test_asm_data[16] = { 0, 0, 2, 0, 
                           0, 0, 0, 4,
                           2, 3, 0, 0, 
                           0, 4, 0, 0 };
char puzzle[16] = { 0, 0, 2, 0, 
                           0, 0, 0, 4,
                           2, 3, 0, 0, 
                           0, 4, 0, 0 };

int print_sudoku_result(int index) {
    int i;
    char str[25];
    puts("puzzle :\t");
    for( i=0 ; i<SIZE ; i++) {   
        int j= *(puzzle+i);
        itoa(j, str, 10);
        puts(str);
    }

    puts("\tc result :\t");
    for( i=0 ; i<SIZE ; i++) {   
        int j= *(test_c_data+i);
        itoa(j, str, 10);
        puts(str);
    }

    puts("\tassembly result :\t");
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
        puts("\tFAIL\n");
    }
    else {
        puts("\tPASS\n");
    }
    return flag;
}


void sudoku_2x2_asm(char *test_asm_data); // TODO, sudoku_2x2_asm.S

void sudoku_2x2_c(char *test_c_data); // TODO, sudoku_2x2_c.S
                        
int main() {
    int err = 0;
    for(int i=0; i<PUZZLE_COUNT; i++){
        memcpy(test_c_data, puzzles[i], 16);
        memcpy(test_asm_data, puzzles[i], 16);
        memcpy(puzzle, puzzles[i], 16);
        sudoku_2x2_c(test_c_data);
        sudoku_2x2_asm(test_asm_data);
        err += print_sudoku_result(i);
    }

    if(err!=0) puts("\tError\n");
    else puts("\tPass\n");

    return 0;
}