#ifndef PCRE2DEMO_LIBRARY_H
#define PCRE2DEMO_LIBRARY_H
#include <stdlib.h>
#include <string.h>

typedef struct match_result {
    int num_matches;
} match_result;

typedef struct regex_list {
    int capacity;
    int length;
    char **regexes;
} regex_list;

regex_list *init_regex_list(int initial_capacity) {
    regex_list *list = malloc(sizeof(regex_list));
    list->capacity = initial_capacity;
    list->length = 0;
    list->regexes = (char **) malloc(list->capacity * sizeof(char *));
    return list;
}

void add_regex(regex_list *regexes, char *regex) {
    if (regexes->length >= regexes->capacity) {
        regexes->capacity *= 2;
        regexes->regexes = realloc(regexes->regexes, regexes->capacity * sizeof(regexes->regexes));
    }
    char *copy = strdup(regex);
    copy[strcspn(copy, "\n")] = 0;
    regexes->regexes[regexes->length++] = copy;
}

match_result run_regex(const char *regex, const char *data);

#endif //PCRE2DEMO_LIBRARY_H
