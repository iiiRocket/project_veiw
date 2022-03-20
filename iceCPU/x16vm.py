from typing import List


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


def mix_with_16(low, high):
    r = (high << 8) + low
    return r


class icePU(object):
    def __init__(self, memory):
        self.memory = memory
        self.registers = dict(
            pa=0,
            a1=0,
            a2=0,
            a3=0,
            c1=0,
            f1=0,
        )

    def run(self):
        while True:
            pa = self.registers['pa']
            op = self.memory[pa]
            # set
            if op == 0:
                self.registers['pa'] += 3
                reg = self.memory[pa + 1]
                value = self.memory[pa + 2]
                self.set_registers(reg, value)
            # load
            elif op == 1:
                self.registers['pa'] += 4
                low_add = self.memory[pa + 1]
                high_add = self.memory[pa + 2]
                address = mix_with_16(low_add, high_add)
                low = self.memory[address]
                reg = self.memory[pa + 3]
                self.set_registers(reg, low)
            # add
            elif op == 2:
                self.registers['pa'] += 4
                reg1 = self.memory[pa + 1]
                reg2 = self.memory[pa + 2]
                reg3 = self.memory[pa + 3]
                value = self.register(reg1) + self.register(reg2)
                value = value & 0b11111111
                self.set_registers(reg3, value)
            # save
            elif op == 3:
                self.registers['pa'] += 4
                reg1 = self.memory[pa + 1]
                value = self.register(reg1)
                value = value & 0xff
                low_add = self.memory[pa + 2]
                high_add = self.memory[pa + 3]
                address = mix_with_16(low_add, high_add)
                self.memory[address] = value
                self.memory[address + 1] = 0x00
            # compare
            elif op == 4:
                self.registers['pa'] += 3
                reg1 = self.memory[pa + 1]
                reg2 = self.memory[pa + 2]
                value1 = self.register(reg1)
                value2 = self.register(reg2)
                if value1 > value2:
                    self.registers['c1'] = 2
                elif value1 == value2:
                    self.registers['c1'] = 1
                else:
                    self.registers['c1'] = 0
            # jump if less
            elif op == 5:
                # print('op:', op)
                self.registers['pa'] += 3
                low_add = self.memory[pa + 1]
                high_add = self.memory[pa + 2]
                address = mix_with_16(low_add, high_add)
                if self.registers['c1'] == 0:
                    self.registers['pa'] = address
            # jump
            elif op == 6:
                self.registers['pa'] += 3
                low_add = self.memory[pa + 1]
                high_add = self.memory[pa + 2]
                address = mix_with_16(low_add, high_add)
                self.registers['pa'] = address
            # save_from_register
            elif op == 7:
                self.registers['pa'] += 3
                reg1 = self.memory[pa + 1]
                reg2 = self.memory[pa + 2]
                value = self.register(reg1)
                value = value & 0xff
                address = self.register(reg2)
                self.memory[address] = value
            # set2
            elif op == 8:
                self.registers['pa'] += 4
                reg = self.memory[pa + 1]
                low = self.memory[pa + 2]
                high = self.memory[pa + 3]
                value = mix_with_16(low, high)
                self.set_registers(reg, value)
            # load2
            elif op == 9:
                self.registers['pa'] += 4
                low_add = self.memory[pa + 1]
                high_add = self.memory[pa + 2]
                address = mix_with_16(low_add, high_add)
                low = self.memory[address]
                high = self.memory[address + 1]
                value = mix_with_16(low, high)
                reg = self.memory[pa + 3]
                self.set_registers(reg, value)
            # add2
            elif op == 10:
                self.registers['pa'] += 4
                reg1 = self.memory[pa + 1]
                reg2 = self.memory[pa + 2]
                reg3 = self.memory[pa + 3]
                value = self.register(reg1) + self.register(reg2)
                value = value & 0xffff
                self.set_registers(reg3, value)
            # save2
            elif op == 11:
                self.registers['pa'] += 4
                reg = self.memory[pa + 1]
                value = self.register(reg)
                low, high = int_with_16(value)
                low_add = self.memory[pa + 2]
                high_add = self.memory[pa + 3]
                address = mix_with_16(low_add, high_add)
                self.memory[address] = low
                self.memory[address + 1] = high
            # subtract2
            elif op == 12:
                self.registers['pa'] += 4
                reg1 = self.memory[pa + 1]
                reg2 = self.memory[pa + 2]
                reg3 = self.memory[pa + 3]
                value = self.register(reg1) - self.register(reg2)
                value = value & 0xffff
                self.set_registers(reg3, value)
            # load_from_register
            elif op == 13:
                self.registers['pa'] += 3
                reg = self.memory[pa + 1]
                address = self.register(reg)
                low = self.memory[address]
                high = self.memory[address + 1]
                reg = self.memory[pa + 2]
                self.set_registers(reg, low)
            # load_from_register2
            elif op == 14:
                # print('pa:', pa)
                # print('memory:', self.memory[pa])
                # print('memory:', self.memory[pa + 1])
                # print('memory:', self.memory[pa + 2])
                self.registers['pa'] += 3
                reg = self.memory[pa + 1]
                address = self.register(reg)
                low = self.memory[address]
                high = self.memory[address + 1]
                value = mix_with_16(low, high)
                reg = self.memory[pa + 2]
                self.set_registers(reg, value)
            # save_from_register2
            elif op == 15:
                self.registers['pa'] += 3
                reg1 = self.memory[pa + 1]
                reg2 = self.memory[pa + 2]
                value = self.register(reg1)
                low, high = int_with_16(value)
                address = self.register(reg2)
                self.memory[address] = low
                self.memory[address + 1] = high
            # jump_from_register
            elif op == 16:
                self.registers['pa'] += 2
                reg = self.memory[pa + 1]
                address = self.register(reg)
                self.registers['pa'] = address
            elif op == 255:
                self.registers['pa'] += 1
                break

    def register(self, reg):
        if reg == 0:
            v = self.registers['pa']
        if reg == 16:
            v = self.registers['a1']
        if reg == 32:
            v = self.registers['a2']
        if reg == 48:
            v = self.registers['a3']
        if reg == 64:
            v = self.registers['c1']
        if reg == 80:
            v = self.registers['f1']
        return v

    def set_registers(self, reg, value):
        if reg == 0:
            self.registers['pa'] = value
        if reg == 16:
            self.registers['a1'] = value
        if reg == 32:
            self.registers['a2'] = value
        if reg == 48:
            self.registers['a3'] = value
        if reg == 64:
            self.registers['c1'] = value
        if reg == 80:
            self.registers['f1'] = value


def run(memory: List[int]):
    cpu = icePU(memory)
    cpu.run()
