# See LICENSE for license details.

#*****************************************************************************
# sra.S
#-----------------------------------------------------------------------------
#
# Test sra instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 0xffffff12
addi x2, x0, 4
TEST_INSERT_NOPS_5
sra x3, x1, x2
RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
