# Collect values (same as your current logic)
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
$moboModel = (cat /sys/devices/virtual/dmi/id/board_name)

$disk = @(
    df -hT -x tmpfs -x devtmpfs -x squashfs |
    awk 'NR>1 && $1 ~ "^/dev" { print $1 "|" $7 "|" $3 "|" $4 "|" $6 "|" $2 }' |
    ForEach-Object {
        $parts = $_ -split "\|"
        [PSCustomObject]@{
            Filesystem = $parts[0]
            MountPoint = $parts[1]
            Size = $parts[2]
            Used = $parts[3]
            UsePercent = $parts[4]
            Type = $parts[5]
        }
    }
)

# FORCE array even if single item
if ($disk -isnot [System.Array]) {
    $disk = @($disk)
}

$osName = (hostnamectl | awk -F': ' '/Operating System/ {print $2}')
$osVer = (hostnamectl | awk -F': ' '/Kernel/ {print $2}')
$osArch = (hostnamectl | awk -F': ' '/Architecture/ {print $2}')
$upTime = (uptime -p | cut -c 4-)

$ipv4 = (ip -4 addr show | awk '$0 !~ /1o/ && /inet / {print $2}' | cut -d/ -f1 | grep -v '^127\.')
$ipv6 = (ip -6 addr show | awk '/inet6/ && $2 !~ /^::1/ {print $2}' | cut -d/ -f1)
$macAddr = (ip link | awk '/link\/ether/ {print $2}')

# Create structured object
$data = [PSCustomObject]@{
    Processor = [PSCustomObject]@{
        Model = $cpuModel
        Cores = $coreCount
        Architecture = $architecture
    }
    Memory = [PSCustomObject]@{
        Total = $memTotal
        Used = $memUsed
        Available = $memAvailable
        SwapTotal = $swapTotal
        SwapUsed = $swapUsed
        SwapFree = $swapFree
    }
    Motherboard = [PSCustomObject]@{
        Manufacturer = $moboManufacturer
        Model = $moboModel
    }
    Disk = $disk
    OS = [PSCustomObject]@{
        Name = $osName
        Version = $osVer
        Architecture = $osArch
        Uptime = $upTime
    }
    Network = [PSCustomObject]@{
        IPv4 = $ipv4
        IPv6 = $ipv6
        MAC = $macAddr
    }
}

# Output JSON
$data | ConvertTo-Json -Depth 5
