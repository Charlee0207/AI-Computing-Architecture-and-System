# See LICENSE for license details.

#*****************************************************************************
# slt.S
#-----------------------------------------------------------------------------
#
# Test slt instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, -16
addi x2, x0, 16
TEST_INSERT_NOPS_5
slt x3, x1, x2


RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
