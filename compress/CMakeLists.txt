
cmake_minimum_required(VERSION 3.4.1)


set(libjpeg_dir ${CMAKE_SOURCE_DIR}/libjpeg)
set(INC_DIR ${libjpeg_dir}/include)
set(libjpeg_lib_dir ${libjpeg_dir}/prebuilt)

#添加头文件目录
include_directories(${INC_DIR})

set(SOURCE_FILES src/main/cpp/compress.c)

add_library(compress SHARED
            ${SOURCE_FILES})

find_library(log-lib log)
find_library(graphics jnigraphics)

add_library(libjpeg SHARED IMPORTED)
set_target_properties(libjpeg PROPERTIES IMPORTED_LOCATION ${libjpeg_lib_dir}/${ANDROID_ABI}/libjpeg.so)



target_link_libraries(compress libjpeg ${log-lib} ${graphics})

