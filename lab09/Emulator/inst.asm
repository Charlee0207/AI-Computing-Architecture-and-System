lui x10, 0x00000000
addi x10, x10, 0x0000000a
lui x05, 0x00000000
addi x05, x05, 0x00000000
lui x06, 0x00000000
addi x06, x06, 0x00000001
lui x07, 0x00000000
addi x07, x07, 0x00000000
addi t3, a0, -1
bge t3, zero, loop_body
j x0, done
add t2, t0, t1
addi t0, t1
addi t1, t2
addi t3, t3, -1
j x0, fibonacci_loop
addi a0, t2
nop
nop
nop
nop
nop
hcf
