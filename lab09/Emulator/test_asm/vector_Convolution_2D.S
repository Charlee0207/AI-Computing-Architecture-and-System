.data 
## input data size = 2x8x8 
input_data:
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7

.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7
.byte 0 1 2 3 4 5 6 7

## kernel size 2x3x3
kernel_data:
.byte 0 1 2
.byte 0 1 2
.byte 0 1 2

.byte 0 1 2
.byte 0 1 2
.byte 0 1 2

## output data size 1x6x6
output_data:
.byte 0 0 0 0 0 0
.byte 0 0 0 0 0 0
.byte 0 0 0 0 0 0
.byte 0 0 0 0 0 0
.byte 0 0 0 0 0 0
.byte 0 0 0 0 0 0

.text
main:
##
## Finish your homework here :))
## Make sure you use only scalar instruction
##
    li      sp, 10000

    addi	sp, sp, -4
    sw		ra, 0(sp)
    
    la      a0,	input_data
    la      a1,	kernel_data
    la      a2,	output_data
    addi    a3, x0, 8
    addi    a4, x0, 3
    addi    a5, x0, 2
    addi    a6, x0, 6
    addi    a7, x0, 1

    jal     ra, convolution_2D

    lw		ra, 0(sp)
    addi	sp, sp, 4

    #addi    a0, x0, 10
    #addi    a1, x0, 0
    #ecall

    # ---------------------------------
    la      a0,	output_data
    addi    a1, x0, 6
    vle8_v  v1, 0(a0)
    add    a0, a0, a1
    vle8_v  v2, 0(a0)
    add    a0, a0, a1
    vle8_v  v3, 0(a0)
    add    a0, a0, a1
    vle8_v  v4, 0(a0)
    add    a0, a0, a1
    vle8_v  v5, 0(a0)
    add    a0, a0, a1
    vle8_v  v6, 0(a0)

    hcf
    

####################################
##
##  Function convolution_2D
##  Parameter:
##      a0: (N) address of input 2D array N
##      a1: (M) address of kernal M
##      a2: (P) address of output 2D array P
##      a3: (INPUT_SIZE)
##      a4: (KERNEL_SIZE)
##      a5: (KERNEL_DIM)
##      a6: (OUTPUT_SIZE)
##      a7: (OUTPUT_DIM)
##  Return:
##      (void)
##
###################################
convolution_2D:
    ## push
    addi	sp, sp, -36
    sw		s2, 0(sp)
    sw		s3, 4(sp)
    sw		s4, 8(sp)
    sw		s5, 12(sp)
    sw		s6, 16(sp)
    sw		s7, 20(sp)
    sw		s8, 24(sp)
    sw		s9, 28(sp)
    sw		s10, 32(sp)        

    # for loop rows
    addi    s2, x0, 0   
    # i
for_loop_rows:
    # ------------------------------ for_loop_rows
    # for loop columns
    addi    s3, x0, 0   
    # j
for_loop_columns:
    # ---------------------------------- for_loop_columns
    
    # for loop output dims
    addi    s5, x0, 0   
    # o_d
for_loop_output_dims:
    # -------------------------------------- for_loop_output_dims
    # address = t0 = & P[o_d][i][0]
    
    mul     t0, s5, a6      
    # o_d * OUTPUT_SIZE
    add     t0, t0, s2      
    # + i
    
    mul     t0, t0, a6      
    # t0 * OUTPUT_SIZE      

    add     s8, a2, t0      
    # & P[o_d][i][0] -> s8

    vadd_vv v1, v0, v0

    # for loop kernel rows
    addi    s6, x0, 0   
    # m
for_loop_kernel_rows:
    # ------------------------------------------ for_loop_kernel_rows
    # for loop kernel columns
    addi    s7, x0, 0   
    # n
for_loop_kernel_columns:
    # ---------------------------------------------- for_loop_kernel_columns
    # for loop kernel dims
    addi    s4, x0, 0   
    # k_d
for_loop_kernel_dims:
    # -------------------------------------------------- for_loop_kernel_dims

    # address = ((k_d * INPUT_SIZE + ii)* INPUT_SIZE) + jj -> s10 = & N[k_d][ii][jj]

    mul     t1, s4, a3      
    # k_d * INPUT_SIZE      
    # t2 -> ii = i + m
    add     t2, s2, s6
    add     t1, t1, t2
    # t1 -> (k_d * INPUT_SIZE + ii)
    mul     t1, t1, a3      
    # ((k_d * INPUT_SIZE + ii)* INPUT_SIZE) 
    # s7 -> jj = n
    add     t1, t1, s7    
    # ((k_d * INPUT_SIZE + ii)* INPUT_SIZE) + jj
    add     s10, a0, t1     
    # ((k_d * INPUT_SIZE + ii)* INPUT_SIZE) + jj + N_addr

    # address -> s9 = & M[o_d][k_d][m][n]

    mul     t0, s5, a5      
    # o_d * KERNEL_DIM
    add     t0, t0, s4      
    # + k_d
    mul     t0, t0, a4      
    # t0 * KERNEL_SIZE
    add     t0, t0, s6      
    # + m
    mul     t0, t0, a4      
    # o_d * KERNEL_SIZE
    add     t0, t0, s7      
    # + n
    add     s9, a1, t0      
    # & M[o_d][k_d][m][n] -> s9

    # load N[k_d][ii][jj]
    vle8_v   v2, 0(s10)
    # load M[o_d][k_d][m][n]
    lb      t1, 0(s9)
    # calculate N[k_d][ii][jj] * M[o_d][k_d][m][n] -> t2
    vmul_vx  v2, v2, t1
    # load P[o_d][i][j]  
    vadd_vv  v1, v1, v2
    # P[o_d][i][j] += N[k_d][ii][jj] * M[o_d][k_d][m][n]

    # -------------------------------------------------- for_loop_kernel_dims
    addi    s4, s4, 1
    blt     s4, a5, for_loop_kernel_dims
    # ---------------------------------------------- for_loop_kernel_columns
    addi    s7, s7, 1
    blt     s7, a4, for_loop_kernel_columns
    # ------------------------------------------ for_loop_kernel_rows
    addi    s6, s6, 1
    blt     s6, a4, for_loop_kernel_rows

    vse8_v  v1, 0(s8)
    # -------------------------------------- for_loop_output_dims
    addi    s5, s5, 1
    blt     s5, a7, for_loop_output_dims
    # ---------------------------------- for_loop_columns
    addi    s3, s3, 8       
    # 8 element in a vector register
    blt     s3, a6, for_loop_columns
    # ------------------------------ for_loop_rows
    addi    s2, s2, 1
    blt     s2, a6, for_loop_rows

    # pop
    lw		s2, 0(sp)
    lw		s3, 4(sp)
    lw		s4, 8(sp)
    lw		s5, 12(sp)
    lw		s6, 16(sp)
    lw		s7, 20(sp)
    lw		s8, 24(sp)
    lw		s9, 28(sp)
    lw		s10, 32(sp)
    addi	sp, sp, 36
    ret