# OSU - CSE6341

> CSE6341 [programming projects](https://sites.google.com/view/rountev/cse-6341/programming-projects)

📌 Java environment:
```bash
java -version

# (on MacOS)
# java version "21.0.4" 2024-07-16 LTS
# Java(TM) SE Runtime Environment (build 21.0.4+8-LTS-274)
# Java HotSpot(TM) 64-Bit Server VM (build 21.0.4+8-LTS-274, mixed mode, sharing)

# (on OSU coelinux)
# openjdk version "20.0.2" 2023-07-18
# OpenJDK Runtime Environment (build 20.0.2+9-78)
# OpenJDK 64-Bit Server VM (build 20.0.2+9-78, mixed mode, sharing)
```

📌 Necessary environment variables:
```bash
JFLEX_DIR="<path-to-this-repo>/proj/jflex-1.7.0"
CUP_DIR="<path-to-this-repo>/proj/cup"
```

## Notes

### 🚩 Project 0

- 📌 [Instructions](https://drive.google.com/file/d/1UNOdZTYtRG2C8e9sjWE1feOObPvSuTBU/view?usp=sharing)
- 📌 [Dependent files](https://drive.google.com/drive/folders/1Cqnf5uVXdf0XAzXjrXIoilNs31VMRPrB?usp=sharing)
- 📌 Test cases
```bash
cd proj/p1
make

./plan t1
./plan t2
./plan t3
```

### 🚩 Project 1

- 📌 [Instructions](https://drive.google.com/file/d/1WVeXnehwmU_DTfx1pdg_64emQX6lP3A5/view?usp=sharing)
- 📌 [Dependent files](https://drive.google.com/drive/folders/1Cqnf5uVXdf0XAzXjrXIoilNs31VMRPrB?usp=sharing)
- 📌 Test cases
```bash
./scripts/execHw1.sh t_ #  t1  -> t5:  success
./scripts/execHw1.sh s_ #  s1  -> s5:  fail
./scripts/execHw1.sh s_c # s1c -> s5c: success
```

- 📌 Pack it up:
```bash
./scripts/packHw1.sh
```

### 🚩 Project 2

- 📌 [Instructions](https://drive.google.com/file/d/1HC4W7WKcw8b7XV58Ag16YjAdsJV9ujIp/view?usp=sharing)
- 📌 [Dependent files](https://drive.google.com/drive/folders/1Cqnf5uVXdf0XAzXjrXIoilNs31VMRPrB?usp=sharing)
- 📌 Test cases
```bash
cd proj/p2
make

./scripts/execHw2.sh t_ #  t1:  success
./scripts/execHw2.sh s_ #  s1 -> s2:  success
```

- 📌 Pack it up:
```bash
./scripts/packHw2.sh
```

### 🚩 Project 3

- 📌 [Instructions](https://drive.google.com/file/d/14SI58ZKQvvlxwFbCHz_CgMkI5Oqv6zei/view?usp=sharing)
- 📌 [Dependent files](https://drive.google.com/drive/folders/1Cqnf5uVXdf0XAzXjrXIoilNs31VMRPrB?usp=sharing)
- 📌 Test cases
```bash
./scripts/makeHw3.sh

./scripts/execHw3.sh s_ #  s1 -> s7:  success
```

- 📌 Pack it up:
```bash
./scripts/packHw3.sh
```
