# See LICENSE for license details.

#*****************************************************************************
# auipc.S
#-----------------------------------------------------------------------------
#
# Test auipc instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
TEST_INSERT_NOPS_2
auipc x1, 0x000fffff

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
