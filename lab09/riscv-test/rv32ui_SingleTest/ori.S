# See LICENSE for license details.

#*****************************************************************************
# ori.S
#-----------------------------------------------------------------------------
#
# Test ori instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 0x0000070f
TEST_INSERT_NOPS_5
ori x3, x1, 0x000000f0


RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
