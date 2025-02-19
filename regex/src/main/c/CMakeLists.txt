cmake_minimum_required(VERSION 3.30)

set(project_name pcre2demo)
set(target_lib ${project_name}-lib)
project(${project_name} C)

set(CMAKE_C_STANDARD 11)

add_library(${target_lib} SHARED pcre2demo.c pcre2demo.h)

find_path(LIBPCRE2_HEADER_PATH NAMES pcre2.h)
message("LIBPCRE2_HEADER_PATH = ${LIBPCRE2_HEADER_PATH}")

target_include_directories(${target_lib} PUBLIC "${LIBPCRE2_HEADER_PATH}")

add_library(libpcre2 STATIC IMPORTED GLOBAL)
find_library(LIBPCRE2_PATH NAMES libpcre2-8.dylib REQUIRED)
message("LIBPCRE2_PATH = ${LIBPCRE2_PATH}")

set_property(TARGET libpcre2 PROPERTY IMPORTED_LOCATION ${LIBPCRE2_PATH})

add_executable(${project_name} pcre2demo.c)
target_link_libraries(${project_name} PUBLIC libpcre2)
target_include_directories(${project_name} PUBLIC "${LIBPCRE2_HEADER_PATH}")

# Shared Lib
target_include_directories(${target_lib} PUBLIC "${LIBPCRE2_HEADER_PATH}")
target_link_libraries(${target_lib} PUBLIC libpcre2)


# Install library
install(TARGETS ${target_lib} DESTINATION lib/${target_lib})

# Tests
include(CTest)
enable_testing()

add_executable(test_run_regex tests/test_run_regex.c)
set_property(TARGET test_run_regex PROPERTY EXCLUDE_FROM_ALL FALSE)
add_dependencies(test_run_regex ${target_lib})
target_link_libraries(test_run_regex PRIVATE ${target_lib})
add_test(NAME TestRunRegex COMMAND test_run_regex)

add_executable(test_run_regexes tests/test_run_regexes.c)
target_link_libraries(test_run_regexes PRIVATE ${target_lib})
add_test(NAME TestRunRegexes COMMAND test_run_regexes)