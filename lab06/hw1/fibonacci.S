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
