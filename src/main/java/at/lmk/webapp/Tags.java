package at.lmk.webapp;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import j2html.attributes.Attr;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.DomContentJoiner;
import j2html.tags.EmptyTag;
import j2html.tags.InlineStaticResource;
import j2html.tags.Text;
import j2html.tags.UnescapedText;

public interface Tags {

	/**
	 * Generic if-expression to do if'ing inside method calls
	 *
	 * @param <T>       The derived generic parameter type
	 * @param condition the condition to if-on
	 * @param ifValue   the value to return if condition is true
	 * @return value if condition is true, null otherwise
	 */
	public default <T> T iff(boolean condition, T ifValue) {
		return condition ? ifValue : null;
	}

	/**
	 * Generic if-expression to if'ing inside method calls
	 *
	 * @param optional   The item that may be present
	 * @param ifFunction The function that will be called if that optional is
	 *                   present
	 * @param <T>        The derived generic parameter type
	 * @param <U>        The supplying generic parameter type
	 * @return transformed value if condition is true, null otherwise
	 */
	public default <T, U> T iff(Optional<U> optional, Function<U, T> ifFunction) {
		if (Objects.nonNull(optional) && optional.isPresent()) {
			return optional.map(ifFunction).orElse(null);
		}
		return null;
	}

	/**
	 * Like {@link j2html.TagCreator#iff}, but returns else-value instead of null
	 */
	public default <T> T iffElse(boolean condition, T ifValue, T elseValue) {
		return condition ? ifValue : elseValue;
	}

	/**
	 * Returns a Attr.ShortForm object with either id, classes or both, obtained
	 * from parsing the input string using css selector syntax
	 *
	 * @param attrs the string with shortform attributes, only id and class is
	 *              supported
	 * @return a Attr.ShortForm object
	 */
	public default Attr.ShortForm attrs(String attrs) {
		return Attr.shortFormFromAttrsString(attrs);
	}

	/**
	 * Returns the HTML created by concatenating the input elements, separated by
	 * space, in encounter order. Also removes spaces before periods and commas.
	 *
	 * @param stringOrDomObjects the elements to join
	 * @return joined elements as HTML
	 */
	public default UnescapedText join(Object... stringOrDomObjects) {
		return DomContentJoiner.join(" ", true, stringOrDomObjects);
	}

	/**
	 * Creates a DomContent object containing HTML elements from a stream. Intended
	 * usage: {@literal each(numbers.stream().map(n -> li(n.toString())))}
	 *
	 * @param stream the stream of DomContent elements
	 * @return DomContent containing elements from the stream
	 */
	public default DomContent each(Stream<DomContent> stream) {
		return new ContainerTag(null).with(stream);
	}

	/**
	 * Creates a DomContent object containing HTML using a mapping function on a
	 * collection Intended usage: {@literal each(numbers, n -> li(n.toString()))}
	 *
	 * @param <T>        The derived generic parameter type
	 * @param collection the collection to iterate over, ex: a list of values "1, 2,
	 *                   3"
	 * @param mapper     the mapping function, ex:
	 *                   {@literal "n -> li(n.toString())"}
	 * @return DomContent containing mapped data
	 *         {@literal (ex. docs: [li(1), li(2), li(3)])}
	 */
	public default <T> DomContent each(Collection<T> collection, Function<? super T, DomContent> mapper) {
		return tag(null).with(collection.stream().map(mapper));
	}

	public default <I, T> DomContent each(final Map<I, T> map, final Function<Entry<I, T>, DomContent> mapper) {
		return rawHtml(map.entrySet().stream().map(mapper.andThen(DomContent::render)).collect(Collectors.joining()));
	}

	/**
	 * Creates a DomContent object containing HTML using a mapping function on a map
	 * Intended usage:
	 * {@literal each(idsToNames, (id, name) -> li(id + " " + name))}
	 *
	 * @param <I>    The type of the keys
	 * @param <T>    The type of the values
	 * @param map    the map to iterate over, ex: a map of values { 1: "Tom", 2:
	 *               "Dick", 3: "Harry" }
	 * @param mapper the mapping function, ex:
	 *               {@literal "(id, name) -> li(id + " " + name)"}
	 * @return DomContent containing mapped data
	 *         {@literal (ex. docs: [li(1 Tom), li(2 Dick), li(3 Harry)])}
	 */
	public default <I, T> DomContent each(final Map<I, T> map, final BiFunction<I, T, DomContent> mapper) {
		return rawHtml(map.entrySet().stream()
				.map(entry -> mapper.andThen(DomContent::render).apply(entry.getKey(), entry.getValue()))
				.collect(Collectors.joining()));
	}

	/**
	 * Filters a collection to a list, to be used with
	 * {@link j2html.TagCreator#each} Intended usage:
	 * {@literal each(filter(numbers, n -> n % 2 == 0), n -> li(n.toString()))}
	 *
	 * @param <T>        The derived generic parameter type
	 * @param collection the collection to filter, ex: a list of values "1, 2, 3"
	 * @param filter     the filter predicate, {@literal ex: "n -> n % 2 == 0"}
	 * @return the filtered collection as a list (ex. docs: 2)
	 */
	public default <T> List<T> filter(Collection<T> collection, Predicate<? super T> filter) {
		return collection.stream().filter(filter).collect(Collectors.toList());
	}

	/**
	 * Wraps a String in an UnescapedText element
	 *
	 * @param html the input html
	 * @return the input html wrapped in an UnescapedText element
	 */
	public default UnescapedText rawHtml(String html) {
		return new UnescapedText(html);
	}

	/**
	 * Wraps a String in a Text element (does html-escaping)
	 *
	 * @param text the input string
	 * @return the input string, html-escaped
	 */
	public default Text text(String text) {
		return new Text(text);
	}

	/**
	 * Return a complete html document string
	 *
	 * @param htmlTag the html content of a website
	 * @return document declaration and rendered html content
	 */
	public default String document(ContainerTag htmlTag) {
		if (htmlTag.getTagName().equals("html")) {
			return document().render() + htmlTag.render();
		}
		throw new IllegalArgumentException("Only HTML-tag can follow document declaration");
	}

	// Special tags
	public default ContainerTag tag(String tagName) {
		return new ContainerTag(tagName);
	}

	public default EmptyTag emptyTag(String tagName) {
		return new EmptyTag(tagName);
	}

	public default Text fileAsEscapedString(String path) {
		return text(InlineStaticResource.getFileAsString(path));
	}

	public default UnescapedText fileAsString(String path) {
		return rawHtml(InlineStaticResource.getFileAsString(path));
	}

	public default ContainerTag styleWithInlineFile(String path) {
		return InlineStaticResource.get(path, InlineStaticResource.TargetFormat.CSS);
	}

	public default ContainerTag scriptWithInlineFile(String path) {
		return InlineStaticResource.get(path, InlineStaticResource.TargetFormat.JS);
	}

	public default ContainerTag styleWithInlineFile_min(String path) {
		return InlineStaticResource.get(path, InlineStaticResource.TargetFormat.CSS_MIN);
	}

	public default ContainerTag scriptWithInlineFile_min(String path) {
		return InlineStaticResource.get(path, InlineStaticResource.TargetFormat.JS_MIN);
	}

	public default DomContent document() {
		return rawHtml("<!DOCTYPE html>");
	}

	// EmptyTags, generated in class j2html.tags.TagCreatorCodeGenerator
	public default EmptyTag area() {
		return new EmptyTag("area");
	}

	public default EmptyTag area(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("area"), shortAttr);
	}

	public default EmptyTag base() {
		return new EmptyTag("base");
	}

	public default EmptyTag base(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("base"), shortAttr);
	}

	public default EmptyTag br() {
		return new EmptyTag("br");
	}

	public default EmptyTag br(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("br"), shortAttr);
	}

	public default EmptyTag col() {
		return new EmptyTag("col");
	}

	public default EmptyTag col(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("col"), shortAttr);
	}

	public default EmptyTag embed() {
		return new EmptyTag("embed");
	}

	public default EmptyTag embed(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("embed"), shortAttr);
	}

	public default EmptyTag hr() {
		return new EmptyTag("hr");
	}

	public default EmptyTag hr(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("hr"), shortAttr);
	}

	public default EmptyTag img() {
		return new EmptyTag("img");
	}

	public default EmptyTag img(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("img"), shortAttr);
	}

	public default EmptyTag input() {
		return new EmptyTag("input");
	}

	public default EmptyTag input(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("input"), shortAttr);
	}

	public default EmptyTag keygen() {
		return new EmptyTag("keygen");
	}

	public default EmptyTag keygen(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("keygen"), shortAttr);
	}

	public default EmptyTag link() {
		return new EmptyTag("link");
	}

	public default EmptyTag link(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("link"), shortAttr);
	}

	public default EmptyTag meta() {
		return new EmptyTag("meta");
	}

	public default EmptyTag meta(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("meta"), shortAttr);
	}

	public default EmptyTag param() {
		return new EmptyTag("param");
	}

	public default EmptyTag param(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("param"), shortAttr);
	}

	public default EmptyTag source() {
		return new EmptyTag("source");
	}

	public default EmptyTag source(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("source"), shortAttr);
	}

	public default EmptyTag track() {
		return new EmptyTag("track");
	}

	public default EmptyTag track(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("track"), shortAttr);
	}

	public default EmptyTag wbr() {
		return new EmptyTag("wbr");
	}

	public default EmptyTag wbr(Attr.ShortForm shortAttr) {
		return Attr.addTo(new EmptyTag("wbr"), shortAttr);
	}

	// ContainerTags, generated in class j2html.tags.TagCreatorCodeGenerator
	public default ContainerTag a() {
		return new ContainerTag("a");
	}

	public default ContainerTag a(String text) {
		return new ContainerTag("a").withText(text);
	}

	public default ContainerTag a(DomContent... dc) {
		return new ContainerTag("a").with(dc);
	}

	public default ContainerTag a(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("a"), shortAttr);
	}

	public default ContainerTag a(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("a").withText(text), shortAttr);
	}

	public default ContainerTag a(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("a").with(dc), shortAttr);
	}

	public default ContainerTag abbr() {
		return new ContainerTag("abbr");
	}

	public default ContainerTag abbr(String text) {
		return new ContainerTag("abbr").withText(text);
	}

	public default ContainerTag abbr(DomContent... dc) {
		return new ContainerTag("abbr").with(dc);
	}

	public default ContainerTag abbr(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("abbr"), shortAttr);
	}

	public default ContainerTag abbr(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("abbr").withText(text), shortAttr);
	}

	public default ContainerTag abbr(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("abbr").with(dc), shortAttr);
	}

	public default ContainerTag address() {
		return new ContainerTag("address");
	}

	public default ContainerTag address(String text) {
		return new ContainerTag("address").withText(text);
	}

	public default ContainerTag address(DomContent... dc) {
		return new ContainerTag("address").with(dc);
	}

	public default ContainerTag address(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("address"), shortAttr);
	}

	public default ContainerTag address(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("address").withText(text), shortAttr);
	}

	public default ContainerTag address(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("address").with(dc), shortAttr);
	}

	public default ContainerTag article() {
		return new ContainerTag("article");
	}

	public default ContainerTag article(String text) {
		return new ContainerTag("article").withText(text);
	}

	public default ContainerTag article(DomContent... dc) {
		return new ContainerTag("article").with(dc);
	}

	public default ContainerTag article(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("article"), shortAttr);
	}

	public default ContainerTag article(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("article").withText(text), shortAttr);
	}

	public default ContainerTag article(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("article").with(dc), shortAttr);
	}

	public default ContainerTag aside() {
		return new ContainerTag("aside");
	}

	public default ContainerTag aside(String text) {
		return new ContainerTag("aside").withText(text);
	}

	public default ContainerTag aside(DomContent... dc) {
		return new ContainerTag("aside").with(dc);
	}

	public default ContainerTag aside(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("aside"), shortAttr);
	}

	public default ContainerTag aside(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("aside").withText(text), shortAttr);
	}

	public default ContainerTag aside(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("aside").with(dc), shortAttr);
	}

	public default ContainerTag audio() {
		return new ContainerTag("audio");
	}

	public default ContainerTag audio(String text) {
		return new ContainerTag("audio").withText(text);
	}

	public default ContainerTag audio(DomContent... dc) {
		return new ContainerTag("audio").with(dc);
	}

	public default ContainerTag audio(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("audio"), shortAttr);
	}

	public default ContainerTag audio(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("audio").withText(text), shortAttr);
	}

	public default ContainerTag audio(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("audio").with(dc), shortAttr);
	}

	public default ContainerTag b() {
		return new ContainerTag("b");
	}

	public default ContainerTag b(String text) {
		return new ContainerTag("b").withText(text);
	}

	public default ContainerTag b(DomContent... dc) {
		return new ContainerTag("b").with(dc);
	}

	public default ContainerTag b(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("b"), shortAttr);
	}

	public default ContainerTag b(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("b").withText(text), shortAttr);
	}

	public default ContainerTag b(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("b").with(dc), shortAttr);
	}

	public default ContainerTag bdi() {
		return new ContainerTag("bdi");
	}

	public default ContainerTag bdi(String text) {
		return new ContainerTag("bdi").withText(text);
	}

	public default ContainerTag bdi(DomContent... dc) {
		return new ContainerTag("bdi").with(dc);
	}

	public default ContainerTag bdi(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("bdi"), shortAttr);
	}

	public default ContainerTag bdi(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("bdi").withText(text), shortAttr);
	}

	public default ContainerTag bdi(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("bdi").with(dc), shortAttr);
	}

	public default ContainerTag bdo() {
		return new ContainerTag("bdo");
	}

	public default ContainerTag bdo(String text) {
		return new ContainerTag("bdo").withText(text);
	}

	public default ContainerTag bdo(DomContent... dc) {
		return new ContainerTag("bdo").with(dc);
	}

	public default ContainerTag bdo(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("bdo"), shortAttr);
	}

	public default ContainerTag bdo(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("bdo").withText(text), shortAttr);
	}

	public default ContainerTag bdo(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("bdo").with(dc), shortAttr);
	}

	public default ContainerTag blockquote() {
		return new ContainerTag("blockquote");
	}

	public default ContainerTag blockquote(String text) {
		return new ContainerTag("blockquote").withText(text);
	}

	public default ContainerTag blockquote(DomContent... dc) {
		return new ContainerTag("blockquote").with(dc);
	}

	public default ContainerTag blockquote(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("blockquote"), shortAttr);
	}

	public default ContainerTag blockquote(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("blockquote").withText(text), shortAttr);
	}

	public default ContainerTag blockquote(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("blockquote").with(dc), shortAttr);
	}

	public default ContainerTag body() {
		return new ContainerTag("body");
	}

	public default ContainerTag body(String text) {
		return new ContainerTag("body").withText(text);
	}

	public default ContainerTag body(DomContent... dc) {
		return new ContainerTag("body").with(dc);
	}

	public default ContainerTag body(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("body"), shortAttr);
	}

	public default ContainerTag body(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("body").withText(text), shortAttr);
	}

	public default ContainerTag body(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("body").with(dc), shortAttr);
	}

	public default ContainerTag button() {
		return new ContainerTag("button");
	}

	public default ContainerTag button(String text) {
		return new ContainerTag("button").withText(text);
	}

	public default ContainerTag button(DomContent... dc) {
		return new ContainerTag("button").with(dc);
	}

	public default ContainerTag button(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("button"), shortAttr);
	}

	public default ContainerTag button(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("button").withText(text), shortAttr);
	}

	public default ContainerTag button(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("button").with(dc), shortAttr);
	}

	public default ContainerTag canvas() {
		return new ContainerTag("canvas");
	}

	public default ContainerTag canvas(String text) {
		return new ContainerTag("canvas").withText(text);
	}

	public default ContainerTag canvas(DomContent... dc) {
		return new ContainerTag("canvas").with(dc);
	}

	public default ContainerTag canvas(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("canvas"), shortAttr);
	}

	public default ContainerTag canvas(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("canvas").withText(text), shortAttr);
	}

	public default ContainerTag canvas(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("canvas").with(dc), shortAttr);
	}

	public default ContainerTag caption() {
		return new ContainerTag("caption");
	}

	public default ContainerTag caption(String text) {
		return new ContainerTag("caption").withText(text);
	}

	public default ContainerTag caption(DomContent... dc) {
		return new ContainerTag("caption").with(dc);
	}

	public default ContainerTag caption(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("caption"), shortAttr);
	}

	public default ContainerTag caption(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("caption").withText(text), shortAttr);
	}

	public default ContainerTag caption(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("caption").with(dc), shortAttr);
	}

	public default ContainerTag cite() {
		return new ContainerTag("cite");
	}

	public default ContainerTag cite(String text) {
		return new ContainerTag("cite").withText(text);
	}

	public default ContainerTag cite(DomContent... dc) {
		return new ContainerTag("cite").with(dc);
	}

	public default ContainerTag cite(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("cite"), shortAttr);
	}

	public default ContainerTag cite(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("cite").withText(text), shortAttr);
	}

	public default ContainerTag cite(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("cite").with(dc), shortAttr);
	}

	public default ContainerTag code() {
		return new ContainerTag("code");
	}

	public default ContainerTag code(String text) {
		return new ContainerTag("code").withText(text);
	}

	public default ContainerTag code(DomContent... dc) {
		return new ContainerTag("code").with(dc);
	}

	public default ContainerTag code(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("code"), shortAttr);
	}

	public default ContainerTag code(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("code").withText(text), shortAttr);
	}

	public default ContainerTag code(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("code").with(dc), shortAttr);
	}

	public default ContainerTag colgroup() {
		return new ContainerTag("colgroup");
	}

	public default ContainerTag colgroup(String text) {
		return new ContainerTag("colgroup").withText(text);
	}

	public default ContainerTag colgroup(DomContent... dc) {
		return new ContainerTag("colgroup").with(dc);
	}

	public default ContainerTag colgroup(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("colgroup"), shortAttr);
	}

	public default ContainerTag colgroup(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("colgroup").withText(text), shortAttr);
	}

	public default ContainerTag colgroup(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("colgroup").with(dc), shortAttr);
	}

	public default ContainerTag datalist() {
		return new ContainerTag("datalist");
	}

	public default ContainerTag datalist(String text) {
		return new ContainerTag("datalist").withText(text);
	}

	public default ContainerTag datalist(DomContent... dc) {
		return new ContainerTag("datalist").with(dc);
	}

	public default ContainerTag datalist(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("datalist"), shortAttr);
	}

	public default ContainerTag datalist(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("datalist").withText(text), shortAttr);
	}

	public default ContainerTag datalist(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("datalist").with(dc), shortAttr);
	}

	public default ContainerTag dd() {
		return new ContainerTag("dd");
	}

	public default ContainerTag dd(String text) {
		return new ContainerTag("dd").withText(text);
	}

	public default ContainerTag dd(DomContent... dc) {
		return new ContainerTag("dd").with(dc);
	}

	public default ContainerTag dd(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("dd"), shortAttr);
	}

	public default ContainerTag dd(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("dd").withText(text), shortAttr);
	}

	public default ContainerTag dd(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("dd").with(dc), shortAttr);
	}

	public default ContainerTag del() {
		return new ContainerTag("del");
	}

	public default ContainerTag del(String text) {
		return new ContainerTag("del").withText(text);
	}

	public default ContainerTag del(DomContent... dc) {
		return new ContainerTag("del").with(dc);
	}

	public default ContainerTag del(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("del"), shortAttr);
	}

	public default ContainerTag del(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("del").withText(text), shortAttr);
	}

	public default ContainerTag del(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("del").with(dc), shortAttr);
	}

	public default ContainerTag details() {
		return new ContainerTag("details");
	}

	public default ContainerTag details(String text) {
		return new ContainerTag("details").withText(text);
	}

	public default ContainerTag details(DomContent... dc) {
		return new ContainerTag("details").with(dc);
	}

	public default ContainerTag details(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("details"), shortAttr);
	}

	public default ContainerTag details(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("details").withText(text), shortAttr);
	}

	public default ContainerTag details(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("details").with(dc), shortAttr);
	}

	public default ContainerTag dfn() {
		return new ContainerTag("dfn");
	}

	public default ContainerTag dfn(String text) {
		return new ContainerTag("dfn").withText(text);
	}

	public default ContainerTag dfn(DomContent... dc) {
		return new ContainerTag("dfn").with(dc);
	}

	public default ContainerTag dfn(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("dfn"), shortAttr);
	}

	public default ContainerTag dfn(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("dfn").withText(text), shortAttr);
	}

	public default ContainerTag dfn(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("dfn").with(dc), shortAttr);
	}

	public default ContainerTag dialog() {
		return new ContainerTag("dialog");
	}

	public default ContainerTag dialog(String text) {
		return new ContainerTag("dialog").withText(text);
	}

	public default ContainerTag dialog(DomContent... dc) {
		return new ContainerTag("dialog").with(dc);
	}

	public default ContainerTag dialog(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("dialog"), shortAttr);
	}

	public default ContainerTag dialog(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("dialog").withText(text), shortAttr);
	}

	public default ContainerTag dialog(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("dialog").with(dc), shortAttr);
	}

	public default ContainerTag div() {
		return new ContainerTag("div");
	}

	public default ContainerTag div(String text) {
		return new ContainerTag("div").withText(text);
	}

	public default ContainerTag div(DomContent... dc) {
		return new ContainerTag("div").with(dc);
	}

	public default ContainerTag div(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("div"), shortAttr);
	}

	public default ContainerTag div(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("div").withText(text), shortAttr);
	}

	public default ContainerTag div(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("div").with(dc), shortAttr);
	}

	public default ContainerTag dl() {
		return new ContainerTag("dl");
	}

	public default ContainerTag dl(String text) {
		return new ContainerTag("dl").withText(text);
	}

	public default ContainerTag dl(DomContent... dc) {
		return new ContainerTag("dl").with(dc);
	}

	public default ContainerTag dl(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("dl"), shortAttr);
	}

	public default ContainerTag dl(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("dl").withText(text), shortAttr);
	}

	public default ContainerTag dl(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("dl").with(dc), shortAttr);
	}

	public default ContainerTag dt() {
		return new ContainerTag("dt");
	}

	public default ContainerTag dt(String text) {
		return new ContainerTag("dt").withText(text);
	}

	public default ContainerTag dt(DomContent... dc) {
		return new ContainerTag("dt").with(dc);
	}

	public default ContainerTag dt(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("dt"), shortAttr);
	}

	public default ContainerTag dt(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("dt").withText(text), shortAttr);
	}

	public default ContainerTag dt(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("dt").with(dc), shortAttr);
	}

	public default ContainerTag em() {
		return new ContainerTag("em");
	}

	public default ContainerTag em(String text) {
		return new ContainerTag("em").withText(text);
	}

	public default ContainerTag em(DomContent... dc) {
		return new ContainerTag("em").with(dc);
	}

	public default ContainerTag em(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("em"), shortAttr);
	}

	public default ContainerTag em(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("em").withText(text), shortAttr);
	}

	public default ContainerTag em(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("em").with(dc), shortAttr);
	}

	public default ContainerTag fieldset() {
		return new ContainerTag("fieldset");
	}

	public default ContainerTag fieldset(String text) {
		return new ContainerTag("fieldset").withText(text);
	}

	public default ContainerTag fieldset(DomContent... dc) {
		return new ContainerTag("fieldset").with(dc);
	}

	public default ContainerTag fieldset(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("fieldset"), shortAttr);
	}

	public default ContainerTag fieldset(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("fieldset").withText(text), shortAttr);
	}

	public default ContainerTag fieldset(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("fieldset").with(dc), shortAttr);
	}

	public default ContainerTag figcaption() {
		return new ContainerTag("figcaption");
	}

	public default ContainerTag figcaption(String text) {
		return new ContainerTag("figcaption").withText(text);
	}

	public default ContainerTag figcaption(DomContent... dc) {
		return new ContainerTag("figcaption").with(dc);
	}

	public default ContainerTag figcaption(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("figcaption"), shortAttr);
	}

	public default ContainerTag figcaption(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("figcaption").withText(text), shortAttr);
	}

	public default ContainerTag figcaption(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("figcaption").with(dc), shortAttr);
	}

	public default ContainerTag figure() {
		return new ContainerTag("figure");
	}

	public default ContainerTag figure(String text) {
		return new ContainerTag("figure").withText(text);
	}

	public default ContainerTag figure(DomContent... dc) {
		return new ContainerTag("figure").with(dc);
	}

	public default ContainerTag figure(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("figure"), shortAttr);
	}

	public default ContainerTag figure(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("figure").withText(text), shortAttr);
	}

	public default ContainerTag figure(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("figure").with(dc), shortAttr);
	}

	public default ContainerTag footer() {
		return new ContainerTag("footer");
	}

	public default ContainerTag footer(String text) {
		return new ContainerTag("footer").withText(text);
	}

	public default ContainerTag footer(DomContent... dc) {
		return new ContainerTag("footer").with(dc);
	}

	public default ContainerTag footer(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("footer"), shortAttr);
	}

	public default ContainerTag footer(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("footer").withText(text), shortAttr);
	}

	public default ContainerTag footer(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("footer").with(dc), shortAttr);
	}

	public default ContainerTag form() {
		return new ContainerTag("form");
	}

	public default ContainerTag form(String text) {
		return new ContainerTag("form").withText(text);
	}

	public default ContainerTag form(DomContent... dc) {
		return new ContainerTag("form").with(dc);
	}

	public default ContainerTag form(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("form"), shortAttr);
	}

	public default ContainerTag form(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("form").withText(text), shortAttr);
	}

	public default ContainerTag form(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("form").with(dc), shortAttr);
	}

	public default ContainerTag h1() {
		return new ContainerTag("h1");
	}

	public default ContainerTag h1(String text) {
		return new ContainerTag("h1").withText(text);
	}

	public default ContainerTag h1(DomContent... dc) {
		return new ContainerTag("h1").with(dc);
	}

	public default ContainerTag h1(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("h1"), shortAttr);
	}

	public default ContainerTag h1(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("h1").withText(text), shortAttr);
	}

	public default ContainerTag h1(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("h1").with(dc), shortAttr);
	}

	public default ContainerTag h2() {
		return new ContainerTag("h2");
	}

	public default ContainerTag h2(String text) {
		return new ContainerTag("h2").withText(text);
	}

	public default ContainerTag h2(DomContent... dc) {
		return new ContainerTag("h2").with(dc);
	}

	public default ContainerTag h2(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("h2"), shortAttr);
	}

	public default ContainerTag h2(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("h2").withText(text), shortAttr);
	}

	public default ContainerTag h2(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("h2").with(dc), shortAttr);
	}

	public default ContainerTag h3() {
		return new ContainerTag("h3");
	}

	public default ContainerTag h3(String text) {
		return new ContainerTag("h3").withText(text);
	}

	public default ContainerTag h3(DomContent... dc) {
		return new ContainerTag("h3").with(dc);
	}

	public default ContainerTag h3(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("h3"), shortAttr);
	}

	public default ContainerTag h3(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("h3").withText(text), shortAttr);
	}

	public default ContainerTag h3(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("h3").with(dc), shortAttr);
	}

	public default ContainerTag h4() {
		return new ContainerTag("h4");
	}

	public default ContainerTag h4(String text) {
		return new ContainerTag("h4").withText(text);
	}

	public default ContainerTag h4(DomContent... dc) {
		return new ContainerTag("h4").with(dc);
	}

	public default ContainerTag h4(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("h4"), shortAttr);
	}

	public default ContainerTag h4(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("h4").withText(text), shortAttr);
	}

	public default ContainerTag h4(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("h4").with(dc), shortAttr);
	}

	public default ContainerTag h5() {
		return new ContainerTag("h5");
	}

	public default ContainerTag h5(String text) {
		return new ContainerTag("h5").withText(text);
	}

	public default ContainerTag h5(DomContent... dc) {
		return new ContainerTag("h5").with(dc);
	}

	public default ContainerTag h5(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("h5"), shortAttr);
	}

	public default ContainerTag h5(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("h5").withText(text), shortAttr);
	}

	public default ContainerTag h5(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("h5").with(dc), shortAttr);
	}

	public default ContainerTag h6() {
		return new ContainerTag("h6");
	}

	public default ContainerTag h6(String text) {
		return new ContainerTag("h6").withText(text);
	}

	public default ContainerTag h6(DomContent... dc) {
		return new ContainerTag("h6").with(dc);
	}

	public default ContainerTag h6(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("h6"), shortAttr);
	}

	public default ContainerTag h6(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("h6").withText(text), shortAttr);
	}

	public default ContainerTag h6(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("h6").with(dc), shortAttr);
	}

	public default ContainerTag head() {
		return new ContainerTag("head");
	}

	public default ContainerTag head(String text) {
		return new ContainerTag("head").withText(text);
	}

	public default ContainerTag head(DomContent... dc) {
		return new ContainerTag("head").with(dc);
	}

	public default ContainerTag head(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("head"), shortAttr);
	}

	public default ContainerTag head(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("head").withText(text), shortAttr);
	}

	public default ContainerTag head(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("head").with(dc), shortAttr);
	}

	public default ContainerTag header() {
		return new ContainerTag("header");
	}

	public default ContainerTag header(String text) {
		return new ContainerTag("header").withText(text);
	}

	public default ContainerTag header(DomContent... dc) {
		return new ContainerTag("header").with(dc);
	}

	public default ContainerTag header(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("header"), shortAttr);
	}

	public default ContainerTag header(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("header").withText(text), shortAttr);
	}

	public default ContainerTag header(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("header").with(dc), shortAttr);
	}

	public default ContainerTag html() {
		return new ContainerTag("html");
	}

	public default ContainerTag html(String text) {
		return new ContainerTag("html").withText(text);
	}

	public default ContainerTag html(DomContent... dc) {
		return new ContainerTag("html").with(dc);
	}

	public default ContainerTag html(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("html"), shortAttr);
	}

	public default ContainerTag html(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("html").withText(text), shortAttr);
	}

	public default ContainerTag html(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("html").with(dc), shortAttr);
	}

	public default ContainerTag i() {
		return new ContainerTag("i");
	}

	public default ContainerTag i(String text) {
		return new ContainerTag("i").withText(text);
	}

	public default ContainerTag i(DomContent... dc) {
		return new ContainerTag("i").with(dc);
	}

	public default ContainerTag i(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("i"), shortAttr);
	}

	public default ContainerTag i(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("i").withText(text), shortAttr);
	}

	public default ContainerTag i(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("i").with(dc), shortAttr);
	}

	public default ContainerTag iframe() {
		return new ContainerTag("iframe");
	}

	public default ContainerTag iframe(String text) {
		return new ContainerTag("iframe").withText(text);
	}

	public default ContainerTag iframe(DomContent... dc) {
		return new ContainerTag("iframe").with(dc);
	}

	public default ContainerTag iframe(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("iframe"), shortAttr);
	}

	public default ContainerTag iframe(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("iframe").withText(text), shortAttr);
	}

	public default ContainerTag iframe(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("iframe").with(dc), shortAttr);
	}

	public default ContainerTag ins() {
		return new ContainerTag("ins");
	}

	public default ContainerTag ins(String text) {
		return new ContainerTag("ins").withText(text);
	}

	public default ContainerTag ins(DomContent... dc) {
		return new ContainerTag("ins").with(dc);
	}

	public default ContainerTag ins(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("ins"), shortAttr);
	}

	public default ContainerTag ins(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("ins").withText(text), shortAttr);
	}

	public default ContainerTag ins(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("ins").with(dc), shortAttr);
	}

	public default ContainerTag kbd() {
		return new ContainerTag("kbd");
	}

	public default ContainerTag kbd(String text) {
		return new ContainerTag("kbd").withText(text);
	}

	public default ContainerTag kbd(DomContent... dc) {
		return new ContainerTag("kbd").with(dc);
	}

	public default ContainerTag kbd(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("kbd"), shortAttr);
	}

	public default ContainerTag kbd(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("kbd").withText(text), shortAttr);
	}

	public default ContainerTag kbd(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("kbd").with(dc), shortAttr);
	}

	public default ContainerTag label() {
		return new ContainerTag("label");
	}

	public default ContainerTag label(String text) {
		return new ContainerTag("label").withText(text);
	}

	public default ContainerTag label(DomContent... dc) {
		return new ContainerTag("label").with(dc);
	}

	public default ContainerTag label(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("label"), shortAttr);
	}

	public default ContainerTag label(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("label").withText(text), shortAttr);
	}

	public default ContainerTag label(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("label").with(dc), shortAttr);
	}

	public default ContainerTag legend() {
		return new ContainerTag("legend");
	}

	public default ContainerTag legend(String text) {
		return new ContainerTag("legend").withText(text);
	}

	public default ContainerTag legend(DomContent... dc) {
		return new ContainerTag("legend").with(dc);
	}

	public default ContainerTag legend(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("legend"), shortAttr);
	}

	public default ContainerTag legend(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("legend").withText(text), shortAttr);
	}

	public default ContainerTag legend(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("legend").with(dc), shortAttr);
	}

	public default ContainerTag li() {
		return new ContainerTag("li");
	}

	public default ContainerTag li(String text) {
		return new ContainerTag("li").withText(text);
	}

	public default ContainerTag li(DomContent... dc) {
		return new ContainerTag("li").with(dc);
	}

	public default ContainerTag li(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("li"), shortAttr);
	}

	public default ContainerTag li(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("li").withText(text), shortAttr);
	}

	public default ContainerTag li(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("li").with(dc), shortAttr);
	}

	public default ContainerTag main() {
		return new ContainerTag("main");
	}

	public default ContainerTag main(String text) {
		return new ContainerTag("main").withText(text);
	}

	public default ContainerTag main(DomContent... dc) {
		return new ContainerTag("main").with(dc);
	}

	public default ContainerTag main(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("main"), shortAttr);
	}

	public default ContainerTag main(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("main").withText(text), shortAttr);
	}

	public default ContainerTag main(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("main").with(dc), shortAttr);
	}

	public default ContainerTag map() {
		return new ContainerTag("map");
	}

	public default ContainerTag map(String text) {
		return new ContainerTag("map").withText(text);
	}

	public default ContainerTag map(DomContent... dc) {
		return new ContainerTag("map").with(dc);
	}

	public default ContainerTag map(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("map"), shortAttr);
	}

	public default ContainerTag map(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("map").withText(text), shortAttr);
	}

	public default ContainerTag map(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("map").with(dc), shortAttr);
	}

	public default ContainerTag mark() {
		return new ContainerTag("mark");
	}

	public default ContainerTag mark(String text) {
		return new ContainerTag("mark").withText(text);
	}

	public default ContainerTag mark(DomContent... dc) {
		return new ContainerTag("mark").with(dc);
	}

	public default ContainerTag mark(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("mark"), shortAttr);
	}

	public default ContainerTag mark(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("mark").withText(text), shortAttr);
	}

	public default ContainerTag mark(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("mark").with(dc), shortAttr);
	}

	public default ContainerTag menu() {
		return new ContainerTag("menu");
	}

	public default ContainerTag menu(String text) {
		return new ContainerTag("menu").withText(text);
	}

	public default ContainerTag menu(DomContent... dc) {
		return new ContainerTag("menu").with(dc);
	}

	public default ContainerTag menu(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("menu"), shortAttr);
	}

	public default ContainerTag menu(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("menu").withText(text), shortAttr);
	}

	public default ContainerTag menu(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("menu").with(dc), shortAttr);
	}

	public default ContainerTag menuitem() {
		return new ContainerTag("menuitem");
	}

	public default ContainerTag menuitem(String text) {
		return new ContainerTag("menuitem").withText(text);
	}

	public default ContainerTag menuitem(DomContent... dc) {
		return new ContainerTag("menuitem").with(dc);
	}

	public default ContainerTag menuitem(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("menuitem"), shortAttr);
	}

	public default ContainerTag menuitem(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("menuitem").withText(text), shortAttr);
	}

	public default ContainerTag menuitem(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("menuitem").with(dc), shortAttr);
	}

	public default ContainerTag meter() {
		return new ContainerTag("meter");
	}

	public default ContainerTag meter(String text) {
		return new ContainerTag("meter").withText(text);
	}

	public default ContainerTag meter(DomContent... dc) {
		return new ContainerTag("meter").with(dc);
	}

	public default ContainerTag meter(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("meter"), shortAttr);
	}

	public default ContainerTag meter(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("meter").withText(text), shortAttr);
	}

	public default ContainerTag meter(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("meter").with(dc), shortAttr);
	}

	public default ContainerTag nav() {
		return new ContainerTag("nav");
	}

	public default ContainerTag nav(String text) {
		return new ContainerTag("nav").withText(text);
	}

	public default ContainerTag nav(DomContent... dc) {
		return new ContainerTag("nav").with(dc);
	}

	public default ContainerTag nav(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("nav"), shortAttr);
	}

	public default ContainerTag nav(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("nav").withText(text), shortAttr);
	}

	public default ContainerTag nav(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("nav").with(dc), shortAttr);
	}

	public default ContainerTag noscript() {
		return new ContainerTag("noscript");
	}

	public default ContainerTag noscript(String text) {
		return new ContainerTag("noscript").withText(text);
	}

	public default ContainerTag noscript(DomContent... dc) {
		return new ContainerTag("noscript").with(dc);
	}

	public default ContainerTag noscript(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("noscript"), shortAttr);
	}

	public default ContainerTag noscript(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("noscript").withText(text), shortAttr);
	}

	public default ContainerTag noscript(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("noscript").with(dc), shortAttr);
	}

	public default ContainerTag object() {
		return new ContainerTag("object");
	}

	public default ContainerTag object(String text) {
		return new ContainerTag("object").withText(text);
	}

	public default ContainerTag object(DomContent... dc) {
		return new ContainerTag("object").with(dc);
	}

	public default ContainerTag object(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("object"), shortAttr);
	}

	public default ContainerTag object(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("object").withText(text), shortAttr);
	}

	public default ContainerTag object(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("object").with(dc), shortAttr);
	}

	public default ContainerTag ol() {
		return new ContainerTag("ol");
	}

	public default ContainerTag ol(String text) {
		return new ContainerTag("ol").withText(text);
	}

	public default ContainerTag ol(DomContent... dc) {
		return new ContainerTag("ol").with(dc);
	}

	public default ContainerTag ol(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("ol"), shortAttr);
	}

	public default ContainerTag ol(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("ol").withText(text), shortAttr);
	}

	public default ContainerTag ol(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("ol").with(dc), shortAttr);
	}

	public default ContainerTag optgroup() {
		return new ContainerTag("optgroup");
	}

	public default ContainerTag optgroup(String text) {
		return new ContainerTag("optgroup").withText(text);
	}

	public default ContainerTag optgroup(DomContent... dc) {
		return new ContainerTag("optgroup").with(dc);
	}

	public default ContainerTag optgroup(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("optgroup"), shortAttr);
	}

	public default ContainerTag optgroup(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("optgroup").withText(text), shortAttr);
	}

	public default ContainerTag optgroup(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("optgroup").with(dc), shortAttr);
	}

	public default ContainerTag option() {
		return new ContainerTag("option");
	}

	public default ContainerTag option(String text) {
		return new ContainerTag("option").withText(text);
	}

	public default ContainerTag option(DomContent... dc) {
		return new ContainerTag("option").with(dc);
	}

	public default ContainerTag option(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("option"), shortAttr);
	}

	public default ContainerTag option(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("option").withText(text), shortAttr);
	}

	public default ContainerTag option(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("option").with(dc), shortAttr);
	}

	public default ContainerTag output() {
		return new ContainerTag("output");
	}

	public default ContainerTag output(String text) {
		return new ContainerTag("output").withText(text);
	}

	public default ContainerTag output(DomContent... dc) {
		return new ContainerTag("output").with(dc);
	}

	public default ContainerTag output(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("output"), shortAttr);
	}

	public default ContainerTag output(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("output").withText(text), shortAttr);
	}

	public default ContainerTag output(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("output").with(dc), shortAttr);
	}

	public default ContainerTag p() {
		return new ContainerTag("p");
	}

	public default ContainerTag p(String text) {
		return new ContainerTag("p").withText(text);
	}

	public default ContainerTag p(DomContent... dc) {
		return new ContainerTag("p").with(dc);
	}

	public default ContainerTag p(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("p"), shortAttr);
	}

	public default ContainerTag p(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("p").withText(text), shortAttr);
	}

	public default ContainerTag p(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("p").with(dc), shortAttr);
	}

	public default ContainerTag pre() {
		return new ContainerTag("pre");
	}

	public default ContainerTag pre(String text) {
		return new ContainerTag("pre").withText(text);
	}

	public default ContainerTag pre(DomContent... dc) {
		return new ContainerTag("pre").with(dc);
	}

	public default ContainerTag pre(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("pre"), shortAttr);
	}

	public default ContainerTag pre(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("pre").withText(text), shortAttr);
	}

	public default ContainerTag pre(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("pre").with(dc), shortAttr);
	}

	public default ContainerTag progress() {
		return new ContainerTag("progress");
	}

	public default ContainerTag progress(String text) {
		return new ContainerTag("progress").withText(text);
	}

	public default ContainerTag progress(DomContent... dc) {
		return new ContainerTag("progress").with(dc);
	}

	public default ContainerTag progress(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("progress"), shortAttr);
	}

	public default ContainerTag progress(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("progress").withText(text), shortAttr);
	}

	public default ContainerTag progress(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("progress").with(dc), shortAttr);
	}

	public default ContainerTag q() {
		return new ContainerTag("q");
	}

	public default ContainerTag q(String text) {
		return new ContainerTag("q").withText(text);
	}

	public default ContainerTag q(DomContent... dc) {
		return new ContainerTag("q").with(dc);
	}

	public default ContainerTag q(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("q"), shortAttr);
	}

	public default ContainerTag q(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("q").withText(text), shortAttr);
	}

	public default ContainerTag q(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("q").with(dc), shortAttr);
	}

	public default ContainerTag rp() {
		return new ContainerTag("rp");
	}

	public default ContainerTag rp(String text) {
		return new ContainerTag("rp").withText(text);
	}

	public default ContainerTag rp(DomContent... dc) {
		return new ContainerTag("rp").with(dc);
	}

	public default ContainerTag rp(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("rp"), shortAttr);
	}

	public default ContainerTag rp(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("rp").withText(text), shortAttr);
	}

	public default ContainerTag rp(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("rp").with(dc), shortAttr);
	}

	public default ContainerTag rt() {
		return new ContainerTag("rt");
	}

	public default ContainerTag rt(String text) {
		return new ContainerTag("rt").withText(text);
	}

	public default ContainerTag rt(DomContent... dc) {
		return new ContainerTag("rt").with(dc);
	}

	public default ContainerTag rt(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("rt"), shortAttr);
	}

	public default ContainerTag rt(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("rt").withText(text), shortAttr);
	}

	public default ContainerTag rt(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("rt").with(dc), shortAttr);
	}

	public default ContainerTag ruby() {
		return new ContainerTag("ruby");
	}

	public default ContainerTag ruby(String text) {
		return new ContainerTag("ruby").withText(text);
	}

	public default ContainerTag ruby(DomContent... dc) {
		return new ContainerTag("ruby").with(dc);
	}

	public default ContainerTag ruby(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("ruby"), shortAttr);
	}

	public default ContainerTag ruby(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("ruby").withText(text), shortAttr);
	}

	public default ContainerTag ruby(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("ruby").with(dc), shortAttr);
	}

	public default ContainerTag s() {
		return new ContainerTag("s");
	}

	public default ContainerTag s(String text) {
		return new ContainerTag("s").withText(text);
	}

	public default ContainerTag s(DomContent... dc) {
		return new ContainerTag("s").with(dc);
	}

	public default ContainerTag s(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("s"), shortAttr);
	}

	public default ContainerTag s(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("s").withText(text), shortAttr);
	}

	public default ContainerTag s(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("s").with(dc), shortAttr);
	}

	public default ContainerTag samp() {
		return new ContainerTag("samp");
	}

	public default ContainerTag samp(String text) {
		return new ContainerTag("samp").withText(text);
	}

	public default ContainerTag samp(DomContent... dc) {
		return new ContainerTag("samp").with(dc);
	}

	public default ContainerTag samp(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("samp"), shortAttr);
	}

	public default ContainerTag samp(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("samp").withText(text), shortAttr);
	}

	public default ContainerTag samp(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("samp").with(dc), shortAttr);
	}

	public default ContainerTag script() {
		return new ContainerTag("script");
	}

	public default ContainerTag script(String text) {
		return new ContainerTag("script").withText(text);
	}

	public default ContainerTag script(DomContent... dc) {
		return new ContainerTag("script").with(dc);
	}

	public default ContainerTag script(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("script"), shortAttr);
	}

	public default ContainerTag script(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("script").withText(text), shortAttr);
	}

	public default ContainerTag script(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("script").with(dc), shortAttr);
	}

	public default ContainerTag section() {
		return new ContainerTag("section");
	}

	public default ContainerTag section(String text) {
		return new ContainerTag("section").withText(text);
	}

	public default ContainerTag section(DomContent... dc) {
		return new ContainerTag("section").with(dc);
	}

	public default ContainerTag section(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("section"), shortAttr);
	}

	public default ContainerTag section(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("section").withText(text), shortAttr);
	}

	public default ContainerTag section(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("section").with(dc), shortAttr);
	}

	public default ContainerTag select() {
		return new ContainerTag("select");
	}

	public default ContainerTag select(String text) {
		return new ContainerTag("select").withText(text);
	}

	public default ContainerTag select(DomContent... dc) {
		return new ContainerTag("select").with(dc);
	}

	public default ContainerTag select(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("select"), shortAttr);
	}

	public default ContainerTag select(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("select").withText(text), shortAttr);
	}

	public default ContainerTag select(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("select").with(dc), shortAttr);
	}

	public default ContainerTag small() {
		return new ContainerTag("small");
	}

	public default ContainerTag small(String text) {
		return new ContainerTag("small").withText(text);
	}

	public default ContainerTag small(DomContent... dc) {
		return new ContainerTag("small").with(dc);
	}

	public default ContainerTag small(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("small"), shortAttr);
	}

	public default ContainerTag small(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("small").withText(text), shortAttr);
	}

	public default ContainerTag small(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("small").with(dc), shortAttr);
	}

	public default ContainerTag span() {
		return new ContainerTag("span");
	}

	public default ContainerTag span(String text) {
		return new ContainerTag("span").withText(text);
	}

	public default ContainerTag span(DomContent... dc) {
		return new ContainerTag("span").with(dc);
	}

	public default ContainerTag span(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("span"), shortAttr);
	}

	public default ContainerTag span(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("span").withText(text), shortAttr);
	}

	public default ContainerTag span(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("span").with(dc), shortAttr);
	}

	public default ContainerTag strong() {
		return new ContainerTag("strong");
	}

	public default ContainerTag strong(String text) {
		return new ContainerTag("strong").withText(text);
	}

	public default ContainerTag strong(DomContent... dc) {
		return new ContainerTag("strong").with(dc);
	}

	public default ContainerTag strong(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("strong"), shortAttr);
	}

	public default ContainerTag strong(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("strong").withText(text), shortAttr);
	}

	public default ContainerTag strong(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("strong").with(dc), shortAttr);
	}

	public default ContainerTag style() {
		return new ContainerTag("style");
	}

	public default ContainerTag style(String text) {
		return new ContainerTag("style").withText(text);
	}

	public default ContainerTag style(DomContent... dc) {
		return new ContainerTag("style").with(dc);
	}

	public default ContainerTag style(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("style"), shortAttr);
	}

	public default ContainerTag style(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("style").withText(text), shortAttr);
	}

	public default ContainerTag style(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("style").with(dc), shortAttr);
	}

	public default ContainerTag sub() {
		return new ContainerTag("sub");
	}

	public default ContainerTag sub(String text) {
		return new ContainerTag("sub").withText(text);
	}

	public default ContainerTag sub(DomContent... dc) {
		return new ContainerTag("sub").with(dc);
	}

	public default ContainerTag sub(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("sub"), shortAttr);
	}

	public default ContainerTag sub(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("sub").withText(text), shortAttr);
	}

	public default ContainerTag sub(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("sub").with(dc), shortAttr);
	}

	public default ContainerTag summary() {
		return new ContainerTag("summary");
	}

	public default ContainerTag summary(String text) {
		return new ContainerTag("summary").withText(text);
	}

	public default ContainerTag summary(DomContent... dc) {
		return new ContainerTag("summary").with(dc);
	}

	public default ContainerTag summary(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("summary"), shortAttr);
	}

	public default ContainerTag summary(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("summary").withText(text), shortAttr);
	}

	public default ContainerTag summary(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("summary").with(dc), shortAttr);
	}

	public default ContainerTag sup() {
		return new ContainerTag("sup");
	}

	public default ContainerTag sup(String text) {
		return new ContainerTag("sup").withText(text);
	}

	public default ContainerTag sup(DomContent... dc) {
		return new ContainerTag("sup").with(dc);
	}

	public default ContainerTag sup(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("sup"), shortAttr);
	}

	public default ContainerTag sup(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("sup").withText(text), shortAttr);
	}

	public default ContainerTag sup(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("sup").with(dc), shortAttr);
	}

	public default ContainerTag table() {
		return new ContainerTag("table");
	}

	public default ContainerTag table(String text) {
		return new ContainerTag("table").withText(text);
	}

	public default ContainerTag table(DomContent... dc) {
		return new ContainerTag("table").with(dc);
	}

	public default ContainerTag table(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("table"), shortAttr);
	}

	public default ContainerTag table(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("table").withText(text), shortAttr);
	}

	public default ContainerTag table(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("table").with(dc), shortAttr);
	}

	public default ContainerTag tbody() {
		return new ContainerTag("tbody");
	}

	public default ContainerTag tbody(String text) {
		return new ContainerTag("tbody").withText(text);
	}

	public default ContainerTag tbody(DomContent... dc) {
		return new ContainerTag("tbody").with(dc);
	}

	public default ContainerTag tbody(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("tbody"), shortAttr);
	}

	public default ContainerTag tbody(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("tbody").withText(text), shortAttr);
	}

	public default ContainerTag tbody(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("tbody").with(dc), shortAttr);
	}

	public default ContainerTag td() {
		return new ContainerTag("td");
	}

	public default ContainerTag td(String text) {
		return new ContainerTag("td").withText(text);
	}

	public default ContainerTag td(DomContent... dc) {
		return new ContainerTag("td").with(dc);
	}

	public default ContainerTag td(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("td"), shortAttr);
	}

	public default ContainerTag td(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("td").withText(text), shortAttr);
	}

	public default ContainerTag td(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("td").with(dc), shortAttr);
	}

	public default ContainerTag textarea() {
		return new ContainerTag("textarea");
	}

	public default ContainerTag textarea(String text) {
		return new ContainerTag("textarea").withText(text);
	}

	public default ContainerTag textarea(DomContent... dc) {
		return new ContainerTag("textarea").with(dc);
	}

	public default ContainerTag textarea(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("textarea"), shortAttr);
	}

	public default ContainerTag textarea(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("textarea").withText(text), shortAttr);
	}

	public default ContainerTag textarea(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("textarea").with(dc), shortAttr);
	}

	public default ContainerTag tfoot() {
		return new ContainerTag("tfoot");
	}

	public default ContainerTag tfoot(String text) {
		return new ContainerTag("tfoot").withText(text);
	}

	public default ContainerTag tfoot(DomContent... dc) {
		return new ContainerTag("tfoot").with(dc);
	}

	public default ContainerTag tfoot(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("tfoot"), shortAttr);
	}

	public default ContainerTag tfoot(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("tfoot").withText(text), shortAttr);
	}

	public default ContainerTag tfoot(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("tfoot").with(dc), shortAttr);
	}

	public default ContainerTag th() {
		return new ContainerTag("th");
	}

	public default ContainerTag th(String text) {
		return new ContainerTag("th").withText(text);
	}

	public default ContainerTag th(DomContent... dc) {
		return new ContainerTag("th").with(dc);
	}

	public default ContainerTag th(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("th"), shortAttr);
	}

	public default ContainerTag th(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("th").withText(text), shortAttr);
	}

	public default ContainerTag th(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("th").with(dc), shortAttr);
	}

	public default ContainerTag thead() {
		return new ContainerTag("thead");
	}

	public default ContainerTag thead(String text) {
		return new ContainerTag("thead").withText(text);
	}

	public default ContainerTag thead(DomContent... dc) {
		return new ContainerTag("thead").with(dc);
	}

	public default ContainerTag thead(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("thead"), shortAttr);
	}

	public default ContainerTag thead(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("thead").withText(text), shortAttr);
	}

	public default ContainerTag thead(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("thead").with(dc), shortAttr);
	}

	public default ContainerTag time() {
		return new ContainerTag("time");
	}

	public default ContainerTag time(String text) {
		return new ContainerTag("time").withText(text);
	}

	public default ContainerTag time(DomContent... dc) {
		return new ContainerTag("time").with(dc);
	}

	public default ContainerTag time(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("time"), shortAttr);
	}

	public default ContainerTag time(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("time").withText(text), shortAttr);
	}

	public default ContainerTag time(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("time").with(dc), shortAttr);
	}

	public default ContainerTag title() {
		return new ContainerTag("title");
	}

	public default ContainerTag title(String text) {
		return new ContainerTag("title").withText(text);
	}

	public default ContainerTag title(DomContent... dc) {
		return new ContainerTag("title").with(dc);
	}

	public default ContainerTag title(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("title"), shortAttr);
	}

	public default ContainerTag title(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("title").withText(text), shortAttr);
	}

	public default ContainerTag title(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("title").with(dc), shortAttr);
	}

	public default ContainerTag tr() {
		return new ContainerTag("tr");
	}

	public default ContainerTag tr(String text) {
		return new ContainerTag("tr").withText(text);
	}

	public default ContainerTag tr(DomContent... dc) {
		return new ContainerTag("tr").with(dc);
	}

	public default ContainerTag tr(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("tr"), shortAttr);
	}

	public default ContainerTag tr(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("tr").withText(text), shortAttr);
	}

	public default ContainerTag tr(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("tr").with(dc), shortAttr);
	}

	public default ContainerTag u() {
		return new ContainerTag("u");
	}

	public default ContainerTag u(String text) {
		return new ContainerTag("u").withText(text);
	}

	public default ContainerTag u(DomContent... dc) {
		return new ContainerTag("u").with(dc);
	}

	public default ContainerTag u(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("u"), shortAttr);
	}

	public default ContainerTag u(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("u").withText(text), shortAttr);
	}

	public default ContainerTag u(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("u").with(dc), shortAttr);
	}

	public default ContainerTag ul() {
		return new ContainerTag("ul");
	}

	public default ContainerTag ul(String text) {
		return new ContainerTag("ul").withText(text);
	}

	public default ContainerTag ul(DomContent... dc) {
		return new ContainerTag("ul").with(dc);
	}

	public default ContainerTag ul(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("ul"), shortAttr);
	}

	public default ContainerTag ul(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("ul").withText(text), shortAttr);
	}

	public default ContainerTag ul(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("ul").with(dc), shortAttr);
	}

	public default ContainerTag var() {
		return new ContainerTag("var");
	}

	public default ContainerTag var(String text) {
		return new ContainerTag("var").withText(text);
	}

	public default ContainerTag var(DomContent... dc) {
		return new ContainerTag("var").with(dc);
	}

	public default ContainerTag var(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("var"), shortAttr);
	}

	public default ContainerTag var(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("var").withText(text), shortAttr);
	}

	public default ContainerTag var(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("var").with(dc), shortAttr);
	}

	public default ContainerTag video() {
		return new ContainerTag("video");
	}

	public default ContainerTag video(String text) {
		return new ContainerTag("video").withText(text);
	}

	public default ContainerTag video(DomContent... dc) {
		return new ContainerTag("video").with(dc);
	}

	public default ContainerTag video(Attr.ShortForm shortAttr) {
		return Attr.addTo(new ContainerTag("video"), shortAttr);
	}

	public default ContainerTag video(Attr.ShortForm shortAttr, String text) {
		return Attr.addTo(new ContainerTag("video").withText(text), shortAttr);
	}

	public default ContainerTag video(Attr.ShortForm shortAttr, DomContent... dc) {
		return Attr.addTo(new ContainerTag("video").with(dc), shortAttr);
	}

}
