
cmake_minimum_required(VERSION 3.0 FATAL_ERROR)
project(custom_ops)

find_package(Torch REQUIRED)

add_executable(hw2-4 hw2-4.cc)
target_link_libraries(hw2-4 "${TORCH_LIBRARIES}")
set_property(TARGET hw2-4 PROPERTY CXX_STANDARD 17)
