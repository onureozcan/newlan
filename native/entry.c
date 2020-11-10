#include <stdio.h>

extern int new_lan_main();

extern int arg0();

int printNumber();

int main() {
  printf("%d\n", new_lan_main());
}

int printNumber() {
  int number = arg0();
  printf("new lang says : %d\n", number);
  return number;
}