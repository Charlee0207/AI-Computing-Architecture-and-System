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