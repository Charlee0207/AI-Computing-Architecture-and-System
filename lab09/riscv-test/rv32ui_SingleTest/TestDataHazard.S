# See LICENSE for license details.

#*****************************************************************************
# TestDataHazard.S
#-----------------------------------------------------------------------------
#
# Test Data Hazard situation.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U

  #-------------------------------------------------------------
  # ID rs - WB rd Data Hazard tests
  #-------------------------------------------------------------
## rs1 - rd
test1:
  addi gp, x0, 1
  addi t1, x0, 1
  TEST_INSERT_NOPS_5
  addi t2, x0, 1
  TEST_INSERT_NOPS_2
  beq t2, t1, test2
  TEST_INSERT_NOPS_5
  beq x0, x0, fail

## rs2 - rd
test2:
  addi gp, x0, 2
  addi t1, x0, 2
  TEST_INSERT_NOPS_5
  addi t2, x0, 2
  TEST_INSERT_NOPS_2
  beq t1, t2, test3
  TEST_INSERT_NOPS_5
  beq x0, x0, fail
  
  #-------------------------------------------------------------
  # ID rs - MEM rd Data Hazard tests
  #-------------------------------------------------------------
## rs1 - rd
test3:
  addi gp, x0, 3
  addi t1, x0, 3
  TEST_INSERT_NOPS_5
  addi t2, x0, 3
  TEST_INSERT_NOPS_1
  beq t2, t1, test4
  TEST_INSERT_NOPS_5
  beq x0, x0, fail

## rs2 - rd
test4:
  addi gp, x0, 4
  addi t1, x0, 4
  TEST_INSERT_NOPS_5
  addi t2, x0, 4
  TEST_INSERT_NOPS_1
  beq t1, t2, test5
  TEST_INSERT_NOPS_5
  beq x0, x0, fail

  #-------------------------------------------------------------
  # ID rs - EXE rd Data Hazard tests
  #-------------------------------------------------------------
## rs1 - rd
test5:
  addi gp, x0, 5
  addi t1, x0, 5
  TEST_INSERT_NOPS_5
  addi t2, x0, 5
  beq t2, t1, test6
  TEST_INSERT_NOPS_5
  beq x0, x0, fail

## rs2 - rd
test6:
  addi gp, x0, 6
  addi t1, x0, 6
  TEST_INSERT_NOPS_5
  addi t2, x0, 6
  beq t1, t2, test7
  TEST_INSERT_NOPS_5
  beq x0, x0, fail

  #-------------------------------------------------------------
  # EXE rs - MEM rd (Memory Access) Data Hazard tests
  #-------------------------------------------------------------
## rs1 - rd
test7:
  addi gp, x0, 7
  la t1, tdata1
  addi t2, x0, 255
  TEST_INSERT_NOPS_5
  lbu t3, 0(t1)
  beq t3, t2, test8
  TEST_INSERT_NOPS_5
  beq x0, x0, fail

## rs2 - rd
test8:
  addi gp, x0, 8
  la t1, tdata3
  addi t2, x0, 15
  TEST_INSERT_NOPS_5
  lbu t3, 0(t1)
  beq t3, t2, test9
  TEST_INSERT_NOPS_5
  beq x0, x0, fail

  #-------------------------------------------------------------
  # All Test
  #-------------------------------------------------------------

test9:
  addi gp, x0, 9
  addi t3, x0, 1
  la t1, tdata1
  lb t2, 0(t1)
  add t3,t2,t3
  add t3,t3,t2
  sub t3,t2,t3
  beq  t3,x0, test10
  beq  x0,x0, fail

test10:
  addi gp, x0, 10
  la t3, tdata5
  la t1, tdata1
  sw t1, 0(t3)
  add t2,x0,t3
  lw t2, 0(t2)
  lb t2,0(t2)
  lb t1,0(t1)
  beq  t1,t2, pass
  beq  x0,x0, fail
pass:
  addi sp, x0, 0
  beq x0, x0, exit
fail:
  TEST_INSERT_NOPS_5
  addi sp, gp, 0
exit:  
   
RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

tdata1: .byte 0xff
tdata2: .byte 0xf0
tdata3: .byte 0x0f
tdata4: .byte 0x00
tdata5: .word 0x00000000

RVTEST_DATA_END
