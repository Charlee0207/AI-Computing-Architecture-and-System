    .text

_start:
    li a0, 10

    li t0, 0
    li t1, 1
    li t2, 0
    addi t3, a0, -1

fibonacci_loop:
    bge t3, zero, loop_body
    j done

loop_body:
    add t2, t0, t1
    mv t0, t1
    mv t1, t2
    addi t3, t3, -1
    j fibonacci_loop

done:
    mv a0, t2
    hcf