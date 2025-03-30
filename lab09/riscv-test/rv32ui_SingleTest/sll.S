# See LICENSE for license details.

#*****************************************************************************
# sll.S
#-----------------------------------------------------------------------------
#
# Test sll instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 0x000007ff
addi x2, x0, 4
TEST_INSERT_NOPS_5
sll x3, x1, x2


RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
