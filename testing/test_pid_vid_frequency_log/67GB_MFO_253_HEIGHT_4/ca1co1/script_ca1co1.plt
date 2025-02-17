set macro

set terminal postscript enhanced color
set logscale x
set logscale y

my_line_width = "3"
set style line 1 linecolor rgbcolor "#0000AA" linewidth @my_line_width
set style line 2 linecolor rgbcolor "#990000" linewidth @my_line_width
set style line 3 linecolor rgbcolor "#52015b" linewidth @my_line_width
set style line 4 linecolor rgbcolor "#988f03" linewidth @my_line_width
set style line 5 linecolor rgbcolor "#be7400" linewidth @my_line_width
set style line 6 linecolor rgbcolor "#00AA00" linewidth @my_line_width
set style line 7 linecolor rgbcolor "#00b7be" linewidth @my_line_width
set style line 8 linecolor rgbcolor "#808080" linewidth @my_line_width
set style line 9 linecolor rgbcolor "#d26584" linewidth @my_line_width


set ylabel "Frequency" textcolor lt "#0000AA"

#########################################################

NOMEFILE = " (cache element = 1, cover search = 1).log"

set xlabel "Pid" textcolor lt "#0000AA
set title "PIDs frequency analysis (Level 2)".NOMEFILE

#FREQUENZE DEI PID A CONFRONTO NEI VARI PROFILI PER IL LIVELLO 2

set output "./Pid_1_1_2.ps"
plot "./Pid_0.125_1_1_2.log" using 2 with lines ti "Profile 0.125", "./Pid_0.25_1_1_2.log" using 2 with lines ti "Profile 0.25", "./Pid_0.5_1_1_2.log" using 2 with lines ti "Profile 0.5"

set title "PIDs frequency analysis (Level 3)".NOMEFILE

#FREQUENZE DEI PID A CONFRONTO NEI VARI PROFILI PER IL LIVELLO 3

set output "./Pid_1_1_3.ps"
plot "./Pid_0.125_1_1_3.log" using 2 with lines ti "Profile 0.125", "./Pid_0.25_1_1_3.log" using 2 with lines ti "Profile 0.25", "./Pid_0.5_1_1_3.log" using 2 with lines ti "Profile 0.5"

set title "PIDs frequency analysis (Level 4)".NOMEFILE

#FREQUENZE DEI PID A CONFRONTO NEI VARI PROFILI PER IL LIVELLO 4

set output "./Pid_1_1_4.ps"
plot "./Pid_0.125_1_1_4.log" using 2 with lines ti "Profile 0.125", "./Pid_0.25_1_1_4.log" using 2 with lines ti "Profile 0.25", "./Pid_0.5_1_1_4.log" using 2 with lines ti "Profile 0.5"

#########################################################

set xlabel "Vid" textcolor lt "#0000AA
set title "VIDs frequency analysis (Level 2)".NOMEFILE

#FREQUENZE DEI vID A CONFRONTO NEI VARI PROFILI PER IL LIVELLO 2

set output "./Vid_1_1_2.ps"
plot "./Vid_0.125_1_1_2.log" using 2 with lines ti "Profile 0.125", "./Vid_0.25_1_1_2.log" using 2 with lines ti "Profile 0.25", "./Vid_0.5_1_1_2.log" using 2 with lines ti "Profile 0.5"

set title "VIDs frequency analysis (Level 3)".NOMEFILE

#FREQUENZE DEI vID A CONFRONTO NEI VARI PROFILI PER IL LIVELLO 3

set output "./Vid_1_1_3.ps"
plot "./Vid_0.125_1_1_3.log" using 2 with lines ti "Profile 0.125", "./Vid_0.25_1_1_3.log" using 2 with lines ti "Profile 0.25", "./Vid_0.5_1_1_3.log" using 2 with lines ti "Profile 0.5"

set title "VIDs frequency analysis (Level 4)".NOMEFILE

#FREQUENZE DEI vID A CONFRONTO NEI VARI PROFILI PER IL LIVELLO 4

set output "./Vid_1_1_4.ps"
plot "./Vid_0.125_1_1_4.log" using 2 with lines ti "Profile 0.125", "./Vid_0.25_1_1_4.log" using 2 with lines ti "Profile 0.25", "./Vid_0.5_1_1_4.log" using 2 with lines ti "Profile 0.5"
