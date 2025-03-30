.text
main:
li 	sp, 10000  
vadd_vv v3, v1, v2
addi 	t1, t1, 2
vmul_vx v4, v1, t1
vse8_v	v4, 0(sp)
addi	t0, sp, 1
vle8_v	v5, 0(t0)
nop
nop
nop
nop
nop
hcf