# See LICENSE for license details.

#*****************************************************************************
# xori.S
#-----------------------------------------------------------------------------
#
# Test xori instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
lui x1, 0x000ffff0
TEST_INSERT_NOPS_5
xori x3, x1, -1

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
