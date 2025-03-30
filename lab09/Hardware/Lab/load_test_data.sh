if [ -n "$1" ]
then
    if [ "$1" = "Emulator" ]
    then
        cp ../../Emulator/test_asm/inst.asm ./src/main/resource/inst.asm
        cp ../../Emulator/test_asm/inst.hex ./src/main/resource/inst.hex
	    cp ../../Emulator/test_asm/data.hex ./src/main/resource/data.hex
    elif [ "$1" = "-s" ]
    then
        cp ../../riscv-test/out/asm/rv32ui_SingleTest-$2.asm ./src/main/resource/inst.asm
        cp ../../riscv-test/out/hex/text/rv32ui_SingleTest-$2.hex ./src/main/resource/inst.hex
        cp ../../riscv-test/out/hex/data/rv32ui_SingleTest-$2.hex ./src/main/resource/data.hex
    else
        cp ../../riscv-test/out/asm/rv32ui_FullTest-$1.asm ./src/main/resource/inst.asm
        cp ../../riscv-test/out/hex/text/rv32ui_FullTest-$1.hex ./src/main/resource/inst.hex
        cp ../../riscv-test/out/hex/data/rv32ui_FullTest-$1.hex ./src/main/resource/data.hex
    fi
else
    echo "[Error] usage should be: ./test_data.sh <which Test program> (Emulator/(-s) <inst code>)"
fi
