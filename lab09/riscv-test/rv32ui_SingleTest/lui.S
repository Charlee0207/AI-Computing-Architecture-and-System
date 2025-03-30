# See LICENSE for license details.

#*****************************************************************************
# lui.S
#-----------------------------------------------------------------------------
#
# Test lui instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
lui x1, 0x00000008

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
