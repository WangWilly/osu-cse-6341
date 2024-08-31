# OSU - CSE6341

[programming-projects](https://sites.google.com/view/rountev/cse-6341/programming-projects)

Java environment (on MacOS):
```bash
java -version
# java version "21.0.4" 2024-07-16 LTS
# Java(TM) SE Runtime Environment (build 21.0.4+8-LTS-274)
# Java HotSpot(TM) 64-Bit Server VM (build 21.0.4+8-LTS-274, mixed mode, sharing)
```

Environment variables:
```bash
JFLEX_DIR="<path-to-this-repo>/proj/jflex-1.7.0"
CUP_DIR="<path-to-this-repo>/proj/cup"
```

## Notes

### Project 0

- [instructions](https://drive.google.com/file/d/1UNOdZTYtRG2C8e9sjWE1feOObPvSuTBU/view?usp=sharing)
- [file](https://drive.google.com/drive/folders/1Cqnf5uVXdf0XAzXjrXIoilNs31VMRPrB?usp=sharing)

```bash
cd proj/p1
make

./plan t1
./plan t2
./plan t3
```

### Project 1

- [instructions](https://drive.google.com/file/d/1WVeXnehwmU_DTfx1pdg_64emQX6lP3A5/view?usp=sharing)
- [file](https://drive.google.com/drive/folders/1Cqnf5uVXdf0XAzXjrXIoilNs31VMRPrB?usp=sharing)

```bash
cd proj/p1
make

# test cases
./plan t_ #  t1  -> t5:  success
./plan s_ #  s1  -> s5:  fail
./plan s_c # s1c -> s5c: success
```

pack it up:
```bash
./scripts/packHw1.sh
```
