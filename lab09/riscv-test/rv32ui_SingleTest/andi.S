# See LICENSE for license details.

#*****************************************************************************
# andi.S
#-----------------------------------------------------------------------------
#
# Test andi instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 0x000007ff
TEST_INSERT_NOPS_5
andi x2, x1, 0x0000070f


RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END