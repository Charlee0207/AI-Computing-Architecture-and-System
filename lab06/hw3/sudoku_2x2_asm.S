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