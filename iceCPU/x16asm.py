def register_mapper():
    """
    :return:
    """
    d = dict(
        pa=0b00000000,
        a1=0b00010000,
        a2=0b00100000,
        a3=0b00110000,
        c1=0b01000000,
        f1=0b01010000,
    )
    return d


def memory_address(address):
    a = address[1:]
    try:
        m = int(a)
        low_add, high_add = int_with_16(m)
        return low_add, high_add
    except ValueError as e:
        print('error', e)
        return a, a


def preprocess(asm):
    """
    :param asm:
    :return:
    """
    l = []
    for line in asm.split('\n'):
        if ';' in line:
            index = line.index(';')
            line = line[:index]
            l.append(line)
        else:
            l.append(line)
    code = '\n'.join(l)
    return code


def int_with_8(num):
    """
    保留低 8 位
    :param num:
    :return:
    """
    mod = 0b11111111
    return num & mod


def int_with_16(num):
    """
    16位拆成小端表示
    :param num:
    :return:
    """

    mod1 = 0b0000000011111111
    i = num
    low = i & mod1
    high = i >> 8
    return low, high


def machine_code(asm: str):
    memory = []
    label_address = {}
    regs = register_mapper()
    asm = preprocess(asm)
    lines = asm.split('\n')
    offset = 0
    for line in lines:
        if line.strip() == '':
            continue
        code = line.split()
        op = code[0]
        if op == 'set':
            offset += 3
            reg = code[1]
            r = regs[reg]
            value = int(code[2])
            memory.append(0)
            memory.append(r)
            memory.append(value)
        elif op == 'set2':
            offset += 4
            reg = code[1]
            r = regs[reg]
            value = int(code[2])
            low, high = int_with_16(value)
            memory.append(8)
            memory.append(r)
            memory = memory + [low, high]
        elif op == 'load':
            offset += 4
            address = int(code[1][1:])
            low, high = int_with_16(address)
            reg = code[2]
            r = regs[reg]
            memory.append(1)
            memory = memory + [low, high]
            memory.append(r)
        elif op == 'load2':
            offset += 4
            address = int(code[1][1:])
            low, high = int_with_16(address)
            reg = code[2]
            r = regs[reg]
            memory.append(9)
            memory = memory + [low, high]
            memory.append(r)
        elif op == 'save':
            offset += 3
            reg = code[1]
            r = regs[reg]
            address = int(code[2][1:])
            low, high = int_with_16(address)
            memory.append(3)
            memory.append(r)
            memory = memory + [low, high]
        elif op == 'save2':
            offset += 4
            reg = code[1]
            r = regs[reg]
            address = int(code[2][1:])
            low, high = int_with_16(address)
            memory.append(11)
            memory.append(r)
            memory = memory + [low, high]
        elif op == 'add':
            offset += 4
            reg1 = code[1]
            reg2 = code[2]
            reg3 = code[3]
            r1 = regs[reg1]
            r2 = regs[reg2]
            r3 = regs[reg3]
            memory.append(2)
            memory.append(r1)
            memory.append(r2)
            memory.append(r3)
        elif op == 'add2':
            offset += 4
            reg1 = code[1]
            reg2 = code[2]
            reg3 = code[3]
            r1 = regs[reg1]
            r2 = regs[reg2]
            r3 = regs[reg3]
            memory.append(10)
            memory.append(r1)
            memory.append(r2)
            memory.append(r3)
        elif op == 'subtract2':
            offset += 4
            reg1 = code[1]
            reg2 = code[2]
            reg3 = code[3]
            r1 = regs[reg1]
            r2 = regs[reg2]
            r3 = regs[reg3]
            memory.append(12)
            memory.append(r1)
            memory.append(r2)
            memory.append(r3)
        elif op == 'compare':
            offset += 3
            reg1 = code[1]
            reg2 = code[2]
            r1 = regs[reg1]
            r2 = regs[reg2]
            memory.append(4)
            memory.append(r1)
            memory.append(r2)
        elif op == 'jump_if_less':
            offset += 3
            low_add, high_add = memory_address(code[1])
            # address = int(code[1][1:])
            memory.append(5)
            memory.append(low_add)
            memory.append(high_add)
        elif op == 'jump':
            offset += 3
            low_add, high_add = memory_address(code[1])
            # address = int(code[1][1:])
            memory.append(6)
            memory.append(low_add)
            memory.append(high_add)
        elif op == 'jump_from_register':
            offset += 2
            reg = code[1]
            r = regs[reg]
            # address = int(code[1][1:])
            memory.append(16)
            memory.append(r)
        elif op == 'save_from_register':
            offset += 3
            reg1 = code[1]
            reg2 = code[2]
            r1 = regs[reg1]
            r2 = regs[reg2]
            memory.append(7)
            memory.append(r1)
            memory.append(r2)
        elif op == 'save_from_register2':
            offset += 3
            reg1 = code[1]
            reg2 = code[2]
            r1 = regs[reg1]
            r2 = regs[reg2]
            memory.append(15)
            memory.append(r1)
            memory.append(r2)
        elif op == 'load_from_register':
            offset += 3
            reg1 = code[1]
            reg2 = code[2]
            r1 = regs[reg1]
            r2 = regs[reg2]
            memory.append(13)
            memory.append(r1)
            memory.append(r2)
        elif op == 'load_from_register2':
            offset += 3
            reg1 = code[1]
            reg2 = code[2]
            r1 = regs[reg1]
            r2 = regs[reg2]
            memory.append(14)
            memory.append(r1)
            memory.append(r2)
        elif op == '.memory':
            offset += 1021
            memory += [0] * 1021
        elif op == '.return':
            offset += 21
            val = int(code[1])
            """
                set2 a2 x
                set2 a3 2
                add2 a2 a3 a3
                subtract2 f1 a3 f1
                load_from_register2 f1 a2
                jump_from_register a2
            """
            code = [8, 32, 'x', 0, 8, 48, 2, 0, 10, 32, 48, 48, 12, 80, 48, 80, 14, 80, 32, 16, 32]
            code[2] = val
            memory += code
        elif op == '.call':
            offset += 22
            func_name = code[1][1:]
            res = [8, 48, 14, 0, 10, 0, 48, 48, 15, 48, 80, 8, 48, 2, 0, 10, 80, 48, 80, 6]
            memory += res + [func_name, func_name]
        elif op == 'halt':
            offset += 1
            memory.append(255)
        elif op[0] == '@':
            label_address[op[1:]] = offset
    for i, e in enumerate(memory):
        if isinstance(e, str):
            # print(label_address)
            low, high = int_with_16(label_address[e])
            memory[i] = low
            memory[i + 1] = high
    return memory
