# See LICENSE for license details.

#*****************************************************************************
# sltiu.S
#-----------------------------------------------------------------------------
#
# Test sltiu instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 16
TEST_INSERT_NOPS_5
sltiu x3, x1, -16
RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
