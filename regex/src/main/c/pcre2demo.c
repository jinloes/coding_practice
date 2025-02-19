#define _GNU_SOURCE
#define PCRE2_CODE_UNIT_WIDTH 8

#include "pcre2demo.h"

#include <stdio.h>
#include <string.h>
#include <dirent.h>
#include <pcre2.h>
#include <stdbool.h>
#include <sys/time.h>

int matches(pcre2_code *re, PCRE2_SPTR subject, pcre2_match_data *match_data,
            pcre2_match_context *match_context) {
    // Execute the regular expression on the subject string
    int rc = pcre2_match(
        re,
        subject,
        strlen(subject),
        0, // Offset into the string
        0, // No options
        match_data,
        match_context
    );

    // Check if the pattern matched
    if (rc < 0) {
        if (rc == PCRE2_ERROR_NOMATCH) {
            //printf("No matches\n");
        } else {
            printf("Match failed, error code: %d\n", rc);
        }
    } else {
        // Matched groups
        /*PCRE2_UCHAR *result;
        PCRE2_SIZE result_len;
        for (int i = 0; i < rc; i++) {
            pcre2_substring_get_bynumber(match_data, i, &result, &result_len);
            printf("Match %s\n", result);
        }*/
    }

    return rc;
}

int iterateMatches(pcre2_code *re, PCRE2_SPTR subject, pcre2_match_data *match_data,
                   pcre2_match_context *match_context) {
    PCRE2_SIZE *ovector;
    PCRE2_SIZE offset = 0;
    PCRE2_UCHAR *result;
    PCRE2_SIZE result_len;
    int length = strlen(subject);
    int rc;
    int count = 0;

    while ((rc = pcre2_match(re, subject, length, offset, 0, match_data, match_context)
                 > 0)) {
        count++;
        /*for (int i = 0; i < rc; i++) {
            pcre2_substring_get_bynumber(match_data, i, &result, &result_len);
            printf("Match %s\n", result);
        }*/
        ovector = pcre2_get_ovector_pointer(match_data);
        offset = ovector[1];
    }

    return count;
}

int run_pcre2_regex(PCRE2_SPTR regex, PCRE2_SPTR subject, int *success, int *failure, bool useJit) {
    int num_matches = 0;
    int errcode;
    pcre2_jit_stack *jit_stack = NULL;
    PCRE2_SIZE erroffset;
    pcre2_compile_context *compile_context = NULL;
    pcre2_code *re = pcre2_compile(
        regex,
        PCRE2_ZERO_TERMINATED,
        0, // No options
        &errcode,
        &erroffset,
        compile_context
    );

    if (re == NULL) {
        printf("Failed to compile regex pattern\n");
        PCRE2_UCHAR buffer[256];
        pcre2_get_error_message(errcode, buffer, sizeof(buffer));
        printf("PCRE2 compilation failed at offset %d: %s\n", (int) erroffset, buffer);
        ++*failure;
    } else {
        pcre2_match_context *match_context = pcre2_match_context_create(NULL);
        pcre2_match_data *match_data = pcre2_match_data_create_from_pattern(re, NULL);
        if (useJit) {
            int rc = pcre2_jit_compile(re, PCRE2_JIT_COMPLETE);
            // 32k 50MB
            jit_stack = pcre2_jit_stack_create(32 * 1024, 1024 * 1024 * 50, NULL);
            pcre2_jit_stack_assign(match_context, NULL, jit_stack);
        }

        //matches(re, subject, match_data, match_context);
        num_matches += iterateMatches(re, subject, match_data, match_context);
        ++*success;

        pcre2_code_free(re);
        pcre2_match_context_free(match_context);
        pcre2_match_data_free(match_data);
        if (jit_stack != NULL) {
            pcre2_jit_stack_free(jit_stack);
        }
    }

    return num_matches;
}

match_result run_regex(const char *regex, const char *data) {
    int success = 0;
    int failure = 0;
    int num_matches = run_pcre2_regex((PCRE2_SPTR) regex, (PCRE2_SPTR) data, &success, &failure,
                                      true);

    match_result result = {num_matches};
    return result;
}
