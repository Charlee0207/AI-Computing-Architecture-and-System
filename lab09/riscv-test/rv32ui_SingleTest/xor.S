# See LICENSE for license details.

#*****************************************************************************
# xor.S
#-----------------------------------------------------------------------------
#
# Test xor instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
lui x1, 0x000ffff0
addi x2, x0, -1
TEST_INSERT_NOPS_5
xor x3, x1, x2

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
