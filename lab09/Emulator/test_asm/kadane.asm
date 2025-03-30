    .data
arr:    .word -2, 1, -3, 4, -1, 2, 1, -5, 4
n:      .word 9

    .text
    .globl _start

_start:
    la t1, n
    lw t0, 0(t1)

    li a0, -2147483648
    li a1, 0

    li t2, 0

loop:
    bge t2, t0, end_loop

    la t3, arr
    slli t4, t2, 2
    add t3, t3, t4
    lw t5, 0(t3)

    add a1, a1, t5

    blt a1, zero, reset_max_ending_here

update_max:
    blt a0, a1, update_so_far
    j next_element

reset_max_ending_here:
    li a1, 0
    j update_max

update_so_far:
    mv a0, a1

next_element:
    addi t2, t2, 1
    j loop

end_loop:
    hcf