#VARIABLES FROM COMMANDS
$cpuModel = (Get-Content /proc/cpuinfo |
    Where-Object { $_ -like "model name*" } |
    Select-Object -First 1).Split(":")[1].Trim() -replace " with.*$",""
$coreCount = (Get-Content /proc/cpuinfo | Where-Object { $_ -like "processor*" }).Count
$architecture = (uname -m)

$memTotal = (free -h | awk '/Mem:/ {print $2}')
$memUsed = (free -h | awk '/Mem:/ {print $3}')
$memAvailable = (free -h | awk '/Mem:/ {print $7}')
$swapTotal = (free -h | awk '/Swap:/ {print $2}')
$swapUsed = (free -h | awk '/Swap:/ {print $3}')
$swapFree = (free -h | awk '/Swap:/ {print $4}')

$moboManufacturer = (cat /sys/devices/virtual/dmi/id/board_vendor)
$moboModel = (cat sys/devices/virtual/dmi/id/board_name) 

$disk = (df -hT -x tmpfs -x devtmpfs -x squashfs | awk 'NR>1 && $1 ~ "^/dev" { printf "Disk (%s): %s / %s (Used: %s) - %s\n", $7, $4, $3, $6, $2 }')

$osName = (hostnamectl | awk -F': ' '/Operating System/ {print $2}')
$osVer = (hostnamectl | awk -F': ' '/Kernel/ {print $2}')
$osArch = (hostnamectl | awk -F': ' '/Architecture/ {print $2}')
$upTime = (uptime -p | cut -c 4-)
$ipv4 = (ip -4 addr show | awk '$0 !~ /1o/ && /inet / {print $2}' | cut -d/ -f1 | grep -v '^127\.')
$ipv6 = (ip -6 addr show | awk '/inet6/ && $2 !~ /^::1/ {print $2}' | cut -d/ -f1)
$macAddr = (ip link | awk '/link\/ether/ {print $2}')



#PROCESSOR INFO
Write-Output "Processor Info" 
Write-Output "Processor: $cpuModel Cores: $coreCount"
Write-Output "Architecture: $architecture`n"

#RAM INFO
Write-Output "Memory Info"
Write-Output "Memory: $memTotal/$memUsed Available: $memAvailable" 
Write-Output "Swap: $swapTotal/$swapUsed Free: $swapFree"

#MOTHERBOARD INFO
Write-Output "Motherboard Info"
Write-Output "Manufacturer: $moboManufacturer"
Write-Output "Model: $moboModel"

#DISK INFO
Write-Output "$disk"

#OS INFO
Write-Output "Operating System: $osName"
Write-Output "OS Version: $osVer"
Write-Output "Architecture: $osArch"
Write-Output "Uptime: $upTime"
Write-Output "Internet Protocol Version 4: $ipv4"
Write-Output "Internet Protocol Version 6: $ipv6"
Write-Output "Mac Address: $macAddr"
