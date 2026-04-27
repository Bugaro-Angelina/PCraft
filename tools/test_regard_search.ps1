$ErrorActionPreference = 'Stop'

$headers = @{ 'User-Agent' = 'Mozilla/5.0' }
$query = [uri]::EscapeDataString('Intel Core i5-13400F OEM')
$url = "https://www.regard.ru/search?t=$query"

$response = Invoke-WebRequest -Uri $url -Headers $headers -UseBasicParsing
$matches = [regex]::Matches($response.Content, '/product/[0-9]+/[^\"''< ]+')
$links = $matches | ForEach-Object { $_.Value } | Select-Object -Unique

Write-Output "links=$($links.Count)"
$links | Select-Object -First 20
$response.Content | Select-String '/product/' -AllMatches | Select-Object -First 10
