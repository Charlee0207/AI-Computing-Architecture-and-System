OUTPUT_ARCH( "riscv" )
ENTRY(_start)

SECTIONS
{
  . = 0x00000000;
  .text : { *(.text) }
  . = ALIGN(0x8000);
  .data : { *(.data) }
  .bss : { *(.bss) }
  _end = .;
}

