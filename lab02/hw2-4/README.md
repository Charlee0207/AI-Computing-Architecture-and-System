## Run hw2-4

```
## download the library 
## https://download.pytorch.org/libtorch/cpu
## unzip libtorch-cxx11-abi-shared-with-deps-2.2.0+cpu.zip 

mkdir build && cd build

cmake -DCMAKE_PREFIX_PATH=./libtorch ..
cmake --build . --config Release -j ${nproc}

./hw2-4 ../traced_modified_alexnet.pt
```
