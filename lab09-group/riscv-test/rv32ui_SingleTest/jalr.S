# See LICENSE for license details.

#*****************************************************************************
# jalr.S
#-----------------------------------------------------------------------------
#
# Test jalr instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x3, x0, 0x00000001
TEST_INSERT_NOPS_5
jalr x1, 52(x0)
TEST_INSERT_NOPS_5
addi x3, x0, 0x00000000
addi x4, x0, 0x00000001

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
