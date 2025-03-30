import os

input = os.getcwd() + "/src/main/resource/InstMem_wc.txt"
output_mch = os.getcwd() + "/src/main/resource/InstMem.txt"
output_ass = os.getcwd() + "/src/main/resource/InstMem_ass.txt"

i_f = open(input,"r")
o_f_mch = open(output_mch,"w")
o_f_ass = open(output_ass,"w")
i_f_ls = i_f.readlines()

for x in i_f_ls:
    mch = x.replace(" ","")[:32]+"\n"
    if mch[0]=='0' or mch[1]=='1':
      ass = x.split("//")[1].replace("\r","")
      o_f_ass.write(ass)
      o_f_mch.write(mch)

i_f.close()
o_f_ass.close()
o_f_mch.close()
