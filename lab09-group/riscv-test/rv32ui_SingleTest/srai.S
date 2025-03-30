# See LICENSE for license details.

#*****************************************************************************
# srai.S
#-----------------------------------------------------------------------------
#
# Test srai instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 0xffffff12
TEST_INSERT_NOPS_5
srai x3, x1, 4
RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
