while True:
    hex_number = input("Enter a hex number: ")
    binary_number = bin(int(hex_number, 16))[2:].zfill(32)
    print(binary_number)