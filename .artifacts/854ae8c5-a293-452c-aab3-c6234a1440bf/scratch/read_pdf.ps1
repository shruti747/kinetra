$pdfPath = "C:\Users\SCAD\Downloads\kinetra.pdf"
$outDir = "C:\Users\SCAD\AndroidStudioProjects\kinetra\.artifacts\854ae8c5-a293-452c-aab3-c6234a1440bf\scratch"

Add-Type -AssemblyName System.Runtime.WindowsRuntime
$asTaskGeneric = [System.WindowsRuntimeSystemExtensions].GetMethods() | ? { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' }

function Await($task) {
    $asTask = $asTaskGeneric.MakeGenericMethod($task.GetType().GetGenericArguments()[0])
    $netTask = $asTask.Invoke($null, @($task))
    $netTask.Wait()
    return $netTask.Result
}

[Windows.Data.Pdf.PdfDocument, Windows.Foundation, ContentType = WindowsRuntime] | Out-Null

$fileAsync = [Windows.Storage.StorageFile]::GetFileFromPathAsync($pdfPath)
$file = Await $fileAsync

$pdfAsync = [Windows.Data.Pdf.PdfDocument]::LoadFromFileAsync($file)
$pdfDoc = Await $pdfAsync

Write-Host "Total pages: $($pdfDoc.PageCount)"

for ($i = 0; $i -lt $pdfDoc.PageCount; $i++) {
    $page = $pdfDoc.GetPage($i)
    $stream = [Windows.Storage.Streams.InMemoryRandomAccessStream]::New()
    $renderAsync = $page.RenderToStreamAsync($stream)
    Await $renderAsync

    $decoderAsync = [Windows.Graphics.Imaging.BitmapDecoder]::CreateAsync($stream)
    $decoder = Await $decoderAsync

    $bitmapAsync = $decoder.GetSoftwareBitmapAsync()
    $softwareBitmap = Await $bitmapAsync

    # Convert SoftwareBitmap to Bitmap
    $format = [Windows.Graphics.Imaging.BitmapPixelFormat]::Bgra8
    $alphaMode = [Windows.Graphics.Imaging.BitmapAlphaMode]::Premultiplied
    $converted = [Windows.Graphics.Imaging.SoftwareBitmap]::Convert($softwareBitmap, $format, $alphaMode)

    $width = $converted.PixelWidth
    $height = $converted.PixelHeight

    $buffer = [Windows.Storage.Streams.Buffer]::New($width * $height * 4)
    $converted.CopyToBuffer($buffer)

    $outFilePath = Join-Path $outDir "page_$($i+1).png"

    $fs = [System.IO.File]::Open($outFilePath, [System.IO.FileMode]::Create)
    $reader = [Windows.Storage.Streams.DataReader]::FromBuffer($buffer)
    $bytes = New-Object byte[]($buffer.Length)
    $reader.ReadBytes($bytes)
    $fs.Write($bytes, 0, $bytes.Length)
    $fs.Close()
    $reader.Close()

    Write-Host "Saved page $($i+1) to $outFilePath"
}
