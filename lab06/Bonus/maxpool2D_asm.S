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