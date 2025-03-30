# See LICENSE for license details.

#*****************************************************************************
# slli.S
#-----------------------------------------------------------------------------
#
# Test slli instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 0x000007ff
TEST_INSERT_NOPS_5
slli x3, x1, 4


RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
