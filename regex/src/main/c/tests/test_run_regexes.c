#define _GNU_SOURCE
#define PCRE2_CODE_UNIT_WIDTH 8
#define SEC_TO_MS(sec) ((sec)*1000)
#define NS_TO_MS(ns)    ((ns)/1000000)

#include "../pcre2demo.h"

#include <stdio.h>
#include <string.h>
#include <dirent.h>
#include <pcre2.h>
#include <sys/time.h>


char *readFile(char *filename) {
    FILE *f = fopen(filename, "rt");
    fseek(f, 0, SEEK_END);
    long length = ftell(f);
    fseek(f, 0, SEEK_SET);
    char *buffer = malloc(length + 1);
    buffer[length] = '\0';
    fread(buffer, 1, length, f);
    fclose(f);
    return buffer;
}

u_int64_t timeInMilliseconds(void) {
    struct timespec ts;
    timespec_get(&ts, TIME_UTC);
    uint64_t ms = SEC_TO_MS((uint64_t)ts.tv_sec) + NS_TO_MS((uint64_t)ts.tv_nsec);
    return ms;
}

int main() {
    char *test_dir = getenv("TEST_FILE_DIR");
    DIR *dr;
    struct dirent *en;
    dr = opendir(test_dir);
    char *test_paths[10];
    int idx = 0;
    if (dr) {
        while ((en = readdir(dr)) != NULL) {
            char *dir_name = en->d_name;
            if (strcmp(dir_name, ".") != 0 && strcmp(dir_name, "..") != 0) {
                //printf("Dir: %s\n", dir_name);
                char *result;
                asprintf(&result, "%s/%s", test_dir, dir_name);
                test_paths[idx] = result;
                idx++;
            }
        }
        closedir(dr);
    }

    FILE *file = fopen(getenv("REGEX_FILE"), "r");

    regex_list *regex_list = init_regex_list(500);

    if (file != NULL) {
        char *regex = NULL;
        size_t len = 0;
        ssize_t read;

        while ((read = getline(&regex, &len, file)) != -1) {
            add_regex(regex_list, regex);
        }
        fclose(file);
        if (regex) {
            free(regex);
        }
    } else {
        printf("Unable to open file!\n");
    }

    for (int i = 0; i < idx; i++) {
        char *test_path = test_paths[i];
        char *content = readFile(test_path);
        struct timespec start, stop;
        int success = 0;
        int failure = 0;
        int num_matches = 0;

        uint64_t startMs = timeInMilliseconds();
        clock_gettime(CLOCK_MONOTONIC_RAW, &start);

        for (int j = 0; j < regex_list->length; j++) {
            match_result result = run_regex(regex_list->regexes[j], content);
            num_matches += result.num_matches;
        }

        clock_gettime(CLOCK_MONOTONIC_RAW, &stop);
        uint64_t endMs = timeInMilliseconds();
        uint64_t delta_ms = SEC_TO_MS(stop.tv_sec - start.tv_sec) +
                            NS_TO_MS(stop.tv_nsec - start.tv_nsec);

        printf("Test file: %s\n", test_path);
        printf("Num matches: %d\n", num_matches);
        printf("Total regexes: %d\n", regex_list->length);
        printf("Success regexes: %d\n", success);
        printf("Failure regexes: %d\n", failure);
        printf("Elapsed Time (TIMEVAL Milliseconds): %llu\n", delta_ms);
        printf("Elapsed Time (Milliseconds): %ld\n", endMs - startMs);
    }

    printf("Done");

    free(regex_list);
    return 0;
}
