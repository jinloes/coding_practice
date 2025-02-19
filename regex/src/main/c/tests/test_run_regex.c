#include <assert.h>
#include <stdio.h>

#include "../pcre2demo.h"

int main() {
    match_result result = run_regex("(?i)foo", "Foo");
    printf("Matches %d\n", result.num_matches);
    assert(result.num_matches == 1);

    result = run_regex("(?i)fos", "Foo");
    printf("Matches %d", result.num_matches);
    assert(result.num_matches == 0);
}
