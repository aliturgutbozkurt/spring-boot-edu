--[[
  Turns GitHub alert blockquotes into coloured boxes in the PDF.

    > [!NOTE]              > [!TIP]            > [!IMPORTANT]
    > Text...              > Text...           > Text...

  GitHub renders the same Markdown as native alerts, so one source works in both places.
  Box styles are defined in header.tex (environments callout-note, callout-tip, ...).
]]

local titles = {
  tr = { NOTE = "Not", TIP = "İpucu", IMPORTANT = "Önemli", WARNING = "Uyarı", CAUTION = "Dikkat" },
  en = { NOTE = "Note", TIP = "Tip", IMPORTANT = "Important", WARNING = "Warning", CAUTION = "Caution" },
}

local lang = "en"

function Meta(meta)
  if meta.lang then
    lang = pandoc.utils.stringify(meta.lang):sub(1, 2)
  end
end

function BlockQuote(el)
  local first = el.content[1]
  if not first or first.t ~= "Para" or #first.content == 0 then
    return nil
  end
  local marker = pandoc.utils.stringify(first.content[1])
  local kind = marker:match("^%[!(%u+)%]$")
  if not kind or not titles.en[kind] then
    return nil
  end

  -- drop the "[!KIND]" marker and the line break that follows it
  first.content:remove(1)
  while #first.content > 0 and (first.content[1].t == "SoftBreak" or first.content[1].t == "Space") do
    first.content:remove(1)
  end
  if #first.content == 0 then
    el.content:remove(1)
  end

  local title = (titles[lang] or titles.en)[kind]
  local env = "callout-" .. kind:lower()
  local blocks = { pandoc.RawBlock("latex", "\\begin{" .. env .. "}{" .. title .. "}") }
  for _, b in ipairs(el.content) do
    table.insert(blocks, b)
  end
  table.insert(blocks, pandoc.RawBlock("latex", "\\end{" .. env .. "}"))
  return blocks
end

-- Meta must run before BlockQuote so that the language is known.
return {
  { Meta = Meta },
  { BlockQuote = BlockQuote },
}
